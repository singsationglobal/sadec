package com.singsation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Singsation - Password Reset OTP");
            message.setText("Your OTP code is: " + otp + "\n\nValid for 5 minutes.\n\nSingsation Team");
            mailSender.send(message);
            logger.info("OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send OTP email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to Singsation!");
            message.setText("Hi " + name + ",\n\nWelcome to Singsation! Start your karaoke journey today.\n\nSingsation Team");
            mailSender.send(message);
            logger.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send welcome email: {}", e.getMessage());
        }
    }
}