package com.singsation.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            Email from = new Email(fromEmail);
            String subject = "Singsation - Password Reset OTP";
            Email to = new Email(toEmail);
            
            Content content = new Content("text/plain", 
                "Your OTP code is: " + otp + "\n\nValid for 5 minutes.\n\nSingsation Team");
            
            Mail mail = new Mail(from, subject, to, content);
            
            SendGrid sg = new SendGrid(sendgridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("Failed to send email: " + response.getBody());
            }
            
            logger.info("OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send OTP email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            Email from = new Email(fromEmail);
            String subject = "Welcome to Singsation!";
            Email to = new Email(toEmail);
            
            Content content = new Content("text/plain", 
                "Hi " + name + ",\n\nWelcome to Singsation! Start your karaoke journey today.\n\nSingsation Team");
            
            Mail mail = new Mail(from, subject, to, content);
            
            SendGrid sg = new SendGrid(sendgridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("Failed to send email: " + response.getBody());
            }
            
            logger.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send welcome email: {}", e.getMessage());
        }
    }
}