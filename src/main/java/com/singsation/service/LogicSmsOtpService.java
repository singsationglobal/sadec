package com.singsation.service;

import com.singsation.config.LogicSmsConfig;
import com.singsation.util.PhoneNumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogicSmsOtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogicSmsOtpService.class);
    
    @Autowired
    private LogicSmsConfig smsConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final ConcurrentHashMap<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    
    private static class OtpData {
        String otp;
        long expiryTime;
        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
    
    @SuppressWarnings("null")
    public boolean sendOtp(String phoneNumber) {
        try {
            String normalizedNumber = PhoneNumberUtil.normalizeSouthAfricanPhone(phoneNumber);
            if (normalizedNumber == null) {
                logger.error("Invalid phone number format: {}", phoneNumber);
                return false;
            }
            
            String formattedForApi = PhoneNumberUtil.toInternationalFormat(normalizedNumber);
            String otp = String.format("%06d", new Random().nextInt(999999));
            String message = "Your Singsation verification code is: " + otp + ". Valid for 5 minutes.";
            
            String postData = "username=" + smsConfig.getUsername() +
                "&password=" + smsConfig.getPassword() +
                "&mobile=" + formattedForApi +
                "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8) +
                "&Originator=" + smsConfig.getOriginator();
            
            logger.info("Sending OTP to: {}", formattedForApi);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<String> entity = new HttpEntity<>(postData, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                smsConfig.getOtpUrl(),
                HttpMethod.POST,
                entity,
                String.class
            );
            
            String responseBody = response.getBody();
            logger.info("LogicSMS response: {}", responseBody);
            
            if (responseBody != null && responseBody.contains("<Id>")) {
                otpStorage.put(normalizedNumber, new OtpData(otp, System.currentTimeMillis() + 300000));
                logger.info("OTP sent successfully to: {}", normalizedNumber);
                return true;
            } else {
                logger.error("Failed to send OTP. Response: {}", responseBody);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error sending OTP: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean verifyOtp(String phoneNumber, String otp) {
        String normalizedNumber = PhoneNumberUtil.normalizeSouthAfricanPhone(phoneNumber);
        if (normalizedNumber == null) {
            return false;
        }
        
        OtpData otpData = otpStorage.get(normalizedNumber);
        
        if (otpData == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > otpData.expiryTime) {
            otpStorage.remove(normalizedNumber);
            return false;
        }
        
        boolean isValid = otpData.otp.equals(otp);
        if (isValid) {
            otpStorage.remove(normalizedNumber);
        }
        
        return isValid;
    }
}