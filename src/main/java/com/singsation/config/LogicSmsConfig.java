package com.singsation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicSmsConfig {
    
    @Value("${logicsms.otp.url}")
    private String otpUrl;
    
    @Value("${logicsms.username}")
    private String username;
    
    @Value("${logicsms.password}")
    private String password;
    
    @Value("${logicsms.originator:REPLY}")
    private String originator;
    
    public String getOtpUrl() { return otpUrl; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getOriginator() { return originator; }
}