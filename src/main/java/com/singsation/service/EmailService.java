package com.singsation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ✅ Generic method for sending emails
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML content
            
            mailSender.send(message);
            logger.info("✅ Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
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
