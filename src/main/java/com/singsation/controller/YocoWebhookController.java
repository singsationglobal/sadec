package com.singsation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singsation.config.YocoConfig;
import com.singsation.model.WebhookLog;
import com.singsation.repository.WebhookLogRepository;
import com.singsation.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/webhooks/yoco")
public class YocoWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(YocoWebhookController.class);
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private YocoConfig yocoConfig;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private WebhookLogRepository webhookLogRepository;

    private boolean verifyWebhookSignature(String payload, String signatureHeader) {
        if (signatureHeader == null || signatureHeader.isEmpty()) {
            logger.warn("Missing signature header");
            return false;
        }
        
        try {
            String[] parts = signatureHeader.split("=");
            if (parts.length != 2 || !"sha256".equals(parts[0])) {
                logger.warn("Invalid signature format: {}", signatureHeader);
                return false;
            }
            String expectedSignature = parts[1];
            
            String secret = yocoConfig.getWebhookSecret();
            if (secret == null || secret.isEmpty()) {
                logger.error("Webhook secret not configured");
                return false;
            }
            
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            boolean isValid = MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                hexString.toString().getBytes(StandardCharsets.UTF_8)
            );
            
            if (!isValid) {
                logger.warn("Invalid signature: expected={}, computed={}", expectedSignature, hexString.toString());
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("Signature verification failed", e);
            return false;
        }
    }

    private String safeGetText(JsonNode node, String field) {
        if (node == null || !node.has(field)) return "";
        JsonNode value = node.get(field);
        return value != null && !value.isNull() ? value.asText("") : "";
    }
    
    private Long safeGetLong(JsonNode node, String field) {
        if (node == null || !node.has(field)) return null;
        JsonNode value = node.get(field);
        if (value != null && !value.isNull() && value.canConvertToLong()) {
            return value.asLong();
        }
        return null;
    }

    @PostMapping("/create-checkout")
    public ResponseEntity<?> createCheckout(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String songId = request.get("songId");
            String amountStr = request.get("amount");
            
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
            }
            if (songId == null || songId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "songId is required"));
            }
            if (amountStr == null || amountStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "amount is required"));
            }
            
            Long amount;
            try {
                Long.parseLong(userId);
                Long.parseLong(songId);
                amount = Long.parseLong(amountStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId, songId, and amount must be valid numbers"));
            }
            
            if (amount != 5000) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid payment amount. Expected R50.00"));
            }
            
            Map<String, Object> checkoutRequest = new HashMap<>();
            checkoutRequest.put("amount", amount);
            checkoutRequest.put("currency", "ZAR");
            
            Map<String, String> metadata = new HashMap<>();
            metadata.put("userId", userId);
            metadata.put("songId", songId);
            checkoutRequest.put("metadata", metadata);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + yocoConfig.getSecretKey());
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(checkoutRequest, headers);
            
            ResponseEntity<String> yocoResponse = restTemplate.postForEntity(
                yocoConfig.getApiUrl() + "/checkouts",
                entity,
                String.class
            );
            
            if (yocoResponse.getStatusCode() != HttpStatus.OK) {
                logger.error("Yoco API error: {}", yocoResponse.getStatusCode());
                return ResponseEntity.status(502).body(Map.of("error", "Payment service unavailable"));
            }
            
            JsonNode jsonResponse = objectMapper.readTree(yocoResponse.getBody());
            String redirectUrl = safeGetText(jsonResponse, "redirectUrl");
            String checkoutId = safeGetText(jsonResponse, "id");
            
            if (redirectUrl.isEmpty() || checkoutId.isEmpty()) {
                logger.error("Invalid Yoco response: {}", yocoResponse.getBody());
                return ResponseEntity.status(502).body(Map.of("error", "Invalid response from payment service"));
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", redirectUrl);
            response.put("checkoutId", checkoutId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Checkout creation failed", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create checkout: " + e.getMessage()));
        }
    }
    
    @PostMapping("/payment")
    public ResponseEntity<?> handlePaymentWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Yoco-Signature", required = false) String signatureHeader) {
        
        WebhookLog log = null;
        String transactionId = null;
        
        try {
            if (!verifyWebhookSignature(payload, signatureHeader)) {
                logger.warn("Invalid webhook signature - possible spoofing attempt");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid signature"));
            }
            
            JsonNode webhookData = objectMapper.readTree(payload);
            String eventType = safeGetText(webhookData, "type");
            
            JsonNode data = webhookData.get("data");
            if (data != null && data.has("id")) {
                transactionId = safeGetText(data, "id");
            }
            
            if (transactionId != null && !transactionId.isEmpty() && 
                webhookLogRepository.existsByTransactionId(transactionId)) {
                logger.info("Duplicate webhook received for transaction: {}", transactionId);
                return ResponseEntity.ok(Map.of("status", "already_processed"));
            }
            
            log = new WebhookLog();
            log.setPayload(payload);
            log.setReceivedAt(LocalDateTime.now());
            log.setProcessed(false);
            log.setTransactionId(transactionId);
            webhookLogRepository.save(log);
            
            if (!"payment.succeeded".equals(eventType) && !"checkout.completed".equals(eventType)) {
                log.setProcessed(true);
                log.setErrorMessage("Event type not processed: " + eventType);
                webhookLogRepository.save(log);
                return ResponseEntity.ok(Map.of("status", "ignored"));
            }
            
            if (data == null) {
                throw new RuntimeException("No data field in webhook payload");
            }
            
            String paymentId = safeGetText(data, "id");
            String status = safeGetText(data, "status");
            Long amountInCents = safeGetLong(data, "amountInCents");
            
            if (paymentId.isEmpty()) {
                throw new RuntimeException("Missing payment ID in webhook");
            }
            if (amountInCents == null) {
                throw new RuntimeException("Missing amountInCents in webhook");
            }
            
            BigDecimal amount = BigDecimal.valueOf(amountInCents).divide(BigDecimal.valueOf(100));
            
            JsonNode metadata = data.get("metadata");
            if (metadata == null || !metadata.isObject()) {
                throw new RuntimeException("Missing or invalid metadata in webhook");
            }
            
            String userIdStr = safeGetText(metadata, "userId");
            String songIdStr = safeGetText(metadata, "songId");
            
            if (userIdStr.isEmpty() || songIdStr.isEmpty()) {
                throw new RuntimeException("Missing userId or songId in metadata");
            }
            
            Long userId, songId;
            try {
                userId = Long.parseLong(userIdStr);
                songId = Long.parseLong(songIdStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid userId or songId format in metadata");
            }
            
            if (!"SUCCESSFUL".equals(status)) {
                log.setProcessed(true);
                log.setErrorMessage("Payment not successful: " + status);
                webhookLogRepository.save(log);
                return ResponseEntity.ok(Map.of("status", "payment_not_successful"));
            }
            
            var existingPayment = paymentService.getPaymentByTransactionId(paymentId);
            if (existingPayment.isPresent()) {
                log.setProcessed(true);
                log.setErrorMessage("Duplicate payment record");
                webhookLogRepository.save(log);
                return ResponseEntity.ok(Map.of("status", "already_recorded"));
            }
            
            paymentService.grantDownloadAccess(userId, songId, paymentId, amount);
            
            log.setProcessed(true);
            webhookLogRepository.save(log);
            
            logger.info("Payment processed successfully: userId={}, songId={}, amount={}", userId, songId, amount);
            return ResponseEntity.ok(Map.of("status", "success"));
            
        } catch (Exception e) {
            logger.error("Webhook processing failed", e);
            if (log != null) {
                log.setErrorMessage(e.getMessage());
                webhookLogRepository.save(log);
            }
            return ResponseEntity.ok(Map.of("status", "error_logged"));
        }
    }
    
    @GetMapping("/check-access")
    public ResponseEntity<?> checkDownloadAccess(
            @RequestParam @NonNull Long userId,
            @RequestParam @NonNull Long songId) {
        
        boolean hasAccess = paymentService.hasDownloadAccess(userId, songId);
        return ResponseEntity.ok(Map.of("hasAccess", hasAccess));
    }
    
    @GetMapping("/reconcile")
    public ResponseEntity<?> reconcilePayments(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {
        
        String expectedToken = System.getenv("RECONCILE_ADMIN_TOKEN");
        if (expectedToken != null && !expectedToken.isEmpty() && 
            !expectedToken.equals(adminToken)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
        }
        
        List<WebhookLog> unprocessed = webhookLogRepository.findByProcessedFalse();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        for (WebhookLog log : unprocessed) {
            try {
                JsonNode webhookData = objectMapper.readTree(log.getPayload());
                JsonNode data = webhookData.get("data");
                
                if (data == null) continue;
                
                String paymentId = safeGetText(data, "id");
                String status = safeGetText(data, "status");
                Long amountInCents = safeGetLong(data, "amountInCents");
                
                if (paymentId.isEmpty() || amountInCents == null) continue;
                
                BigDecimal amount = BigDecimal.valueOf(amountInCents).divide(BigDecimal.valueOf(100));
                
                JsonNode metadata = data.get("metadata");
                if (metadata == null) continue;
                
                String userIdStr = safeGetText(metadata, "userId");
                String songIdStr = safeGetText(metadata, "songId");
                
                if (userIdStr.isEmpty() || songIdStr.isEmpty()) continue;
                
                Long userId = Long.parseLong(userIdStr);
                Long songId = Long.parseLong(songIdStr);
                
                var existing = paymentService.getPaymentByTransactionId(paymentId);
                if (existing.isPresent()) {
                    log.setProcessed(true);
                    webhookLogRepository.save(log);
                    successCount++;
                    continue;
                }
                
                if ("SUCCESSFUL".equals(status)) {
                    paymentService.grantDownloadAccess(userId, songId, paymentId, amount);
                    log.setProcessed(true);
                    webhookLogRepository.save(log);
                    successCount++;
                } else {
                    log.setErrorMessage("Payment not successful: " + status);
                    webhookLogRepository.save(log);
                    failCount++;
                }
            } catch (Exception e) {
                log.setErrorMessage("Retry failed: " + e.getMessage());
                webhookLogRepository.save(log);
                errors.add("Log #" + log.getId() + ": " + e.getMessage());
                failCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", unprocessed.size());
        result.put("success", successCount);
        result.put("failed", failCount);
        if (!errors.isEmpty()) {
            result.put("errors", errors);
        }
        
        return ResponseEntity.ok(result);
    }
}