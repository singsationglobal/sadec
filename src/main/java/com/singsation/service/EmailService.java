package com.singsation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${app.email.from-address}")
    private String fromEmail;

    @Value("${app.email.from-name:Singsation}")
    private String fromName;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ✅ Sends email via Brevo HTTPS API (works on Render/Cloud Run — no SMTP port block)
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            Map<String, Object> sender = new HashMap<>();
            sender.put("name", fromName);
            sender.put("email", fromEmail);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", to);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", sender);
            body.put("to", List.of(recipient));
            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("api-key", brevoApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Email sent via Brevo to: {} (status {})", to, response.getStatusCode().value());
            } else {
                logger.error("❌ Brevo returned non-2xx: {} — {}", response.getStatusCode().value(), response.getBody());
                throw new RuntimeException("Failed to send email: " + response.getStatusCode());
            }

        } catch (RestClientException e) {
            logger.error("❌ Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // ✅ Keep this for backward compatibility - used by your existing code
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Singsation - Password Reset OTP";
        String content = "Your OTP code is: " + otp + "\n\nValid for 5 minutes.\n\nSingsation Team";
        sendEmail(toEmail, subject, content);
    }

    // ✅ Keep this for backward compatibility - used by your existing code
    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Singsation!";
        String content = "Hi " + name + ",\n\nWelcome to Singsation! Start your karaoke journey today.\n\nSingsation Team";
        sendEmail(toEmail, subject, content);
    }
}
