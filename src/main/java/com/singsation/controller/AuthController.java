package com.singsation.controller;

import com.singsation.dto.LoginRequest;
import com.singsation.dto.RegisterRequest;
import com.singsation.model.User;
import com.singsation.service.UserService;
import com.singsation.security.JwtUtil;
import com.singsation.service.LogicSmsOtpService;
import com.singsation.service.EmailService;
import com.singsation.util.PhoneNumberUtil;
import com.singsation.service.UserActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserActivityLogService userActivityLogService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private LogicSmsOtpService logicSmsOtpService;
    
    @Autowired
    private EmailService emailService;
    
    private final ConcurrentHashMap<String, SignupOtpData> signupOtpStorage = new ConcurrentHashMap<>();
    
    private static class SignupOtpData {
        String otp;
        long expiryTime;
        String userId;
        String token;
        String resetToken;
        
        SignupOtpData(String otp, long expiryTime, String userId, String token, String resetToken) {
            this.otp = otp;
            this.expiryTime = expiryTime;
            this.userId = userId;
            this.token = token;
            this.resetToken = resetToken;
        }
    }
    
    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("surname", user.getSurname() != null ? user.getSurname() : "");
        userMap.put("email", user.getEmail());
        userMap.put("userid", user.getUserid() != null ? user.getUserid() : "SING_" + user.getId());
        userMap.put("contact", user.getContact() != null ? user.getContact() : "");
        userMap.put("winner", user.getWinner() != null ? user.getWinner() : "www.singsationsadec.com");
        userMap.put("alternativeContact", user.getAlternativeContact() != null ? user.getAlternativeContact() : "");
        userMap.put("province", user.getProvince() != null ? user.getProvince() : "");
        userMap.put("age", user.getAge() != null ? user.getAge() : 0);
        userMap.put("hasCompletedEntry", user.isHasCompletedEntry());
        userMap.put("signupMethod", user.getSignupMethod());
        return userMap;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String username = request.getUsername();
            Optional<User> userOpt;
            
            if (PhoneNumberUtil.isEmail(username)) {
                userOpt = userService.findByEmail(username);
            } else {
                String normalizedPhone = PhoneNumberUtil.normalizeSouthAfricanPhone(username);
                if (normalizedPhone == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format"));
                }
                userOpt = userService.findByContact(normalizedPhone);
            }
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (userService.checkPassword(request.getPassword(), user.getPassword())) {
                    String token = jwtUtil.generateToken(user.getEmail());
                    
                    userActivityLogService.logActivity(user, "LOGIN", "User logged in successfully", httpRequest);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("userId", String.valueOf(user.getId()));
                    response.put("hasCompletedEntry", user.isHasCompletedEntry());
                    response.put("signupMethod", user.getSignupMethod());
                    response.put("user", buildUserResponse(user));
                    
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String emailInput = request.getEmail().trim();
            boolean isEmailSignup = PhoneNumberUtil.isEmail(emailInput);
            String normalizedContact = null;
            
            if (isEmailSignup) {
                if (userService.findByEmail(emailInput).isPresent()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
                }
                if (userService.findByAlternativeContact(emailInput).isPresent()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "This email is already linked to another account as 2-way authentication"));
                }
            } else {
                normalizedContact = PhoneNumberUtil.normalizeSouthAfricanPhone(emailInput);
                if (normalizedContact == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format"));
                }
                if (userService.findByContact(normalizedContact).isPresent()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Phone number already registered"));
                }
                if (userService.findByAlternativeContact(normalizedContact).isPresent()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "This phone number is already linked to another account as 2-way authentication"));
                }
            }
            
            User user = new User();
            user.setName(request.getName());
            user.setSurname(request.getSurname());
            user.setSignupMethod(isEmailSignup ? "EMAIL" : "CONTACT");
            
            if (isEmailSignup) {
                user.setEmail(emailInput);
                user.setContact(emailInput);
            } else {
                user.setContact(normalizedContact);
                user.setEmail(normalizedContact + "@temp.singsation.local");
            }
            
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(request.getPassword()));
            
            user.setUserid("SING_" + System.currentTimeMillis());
            user.setWinner("www.singsationsadec.com");
            user.setHasCompletedEntry(false);
            
            User savedUser = userService.registerUser(user);
            String token = jwtUtil.generateToken(savedUser.getEmail());
            
            String otp = String.format("%06d", new java.util.Random().nextInt(999999));
            String resetToken = UUID.randomUUID().toString();
            
            String storageKey = isEmailSignup ? emailInput : normalizedContact;
            
            // ✅ FIXED: Changed from 300000 to 3600000 (5 min to 60 min)
            signupOtpStorage.put(storageKey, 
                new SignupOtpData(otp, System.currentTimeMillis() + 3600000, String.valueOf(savedUser.getId()), token, resetToken));
            
            if (isEmailSignup) {
                emailService.sendOtpEmail(savedUser.getEmail(), otp);
            } else {
                logicSmsOtpService.sendOtp(savedUser.getContact());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", String.valueOf(savedUser.getId()));
            response.put("requiresVerification", true);
            response.put("resetToken", resetToken);
            response.put("signupMethod", savedUser.getSignupMethod());
            response.put("hasCompletedEntry", false);
            response.put("user", buildUserResponse(savedUser));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/verify-signup-otp")
    public ResponseEntity<?> verifySignupOtp(@RequestBody Map<String, String> request) {
        String emailOrPhone = request.get("email");
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            emailOrPhone = request.get("contact");
        }
        
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email or phone is required"));
        }
        
        String storageKey = emailOrPhone.trim();
        if (!PhoneNumberUtil.isEmail(storageKey)) {
            storageKey = PhoneNumberUtil.normalizeSouthAfricanPhone(storageKey);
            if (storageKey == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format"));
            }
        }
        
        SignupOtpData data = signupOtpStorage.get(storageKey);
        
        if (data == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No verification found. Please register again."));
        }
        
        if (System.currentTimeMillis() > data.expiryTime) {
            signupOtpStorage.remove(storageKey);
            return ResponseEntity.badRequest().body(Map.of("error", "OTP expired. Please register again."));
        }
        
        String otp = request.get("otp");
        if (!data.otp.equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP"));
        }
        
        signupOtpStorage.remove(storageKey);
        
        Optional<User> userOpt;
        if (PhoneNumberUtil.isEmail(storageKey)) {
            userOpt = userService.findByEmail(storageKey);
        } else {
            userOpt = userService.findByContact(storageKey);
        }
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        
        User user = userOpt.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("verified", true);
        response.put("userId", data.userId);
        response.put("token", data.token);
        response.put("user", buildUserResponse(user));
        response.put("signupMethod", user.getSignupMethod());
        response.put("hasCompletedEntry", user.isHasCompletedEntry());
        response.put("message", "OTP verified successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send-sms-otp")
    public ResponseEntity<?> sendSmsOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
        }
        
        String normalizedPhone = PhoneNumberUtil.normalizeSouthAfricanPhone(phoneNumber);
        if (normalizedPhone == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format"));
        }
        
        Optional<User> userOpt = userService.findByContact(normalizedPhone);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number not registered"));
        }
        
        boolean sent = logicSmsOtpService.sendOtp(normalizedPhone);
        
        if (sent) {
            String resetToken = UUID.randomUUID().toString();
            // ✅ FIXED: Changed from 300000 to 3600000 (5 min to 60 min)
            signupOtpStorage.put(normalizedPhone, 
                new SignupOtpData(null, System.currentTimeMillis() + 3600000, String.valueOf(userOpt.get().getId()), null, resetToken));
            
            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "resetToken", resetToken,
                "phoneNumber", normalizedPhone
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send OTP. Please try again."));
        }
    }
    
    @PostMapping("/verify-sms-otp")
    public ResponseEntity<?> verifySmsOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");
        
        if (phoneNumber == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number and OTP are required"));
        }
        
        String normalizedPhone = PhoneNumberUtil.normalizeSouthAfricanPhone(phoneNumber);
        if (normalizedPhone == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format"));
        }
        
        boolean isValid = logicSmsOtpService.verifyOtp(normalizedPhone, otp);
        
        if (isValid) {
            String newResetToken = UUID.randomUUID().toString();
            SignupOtpData existing = signupOtpStorage.get(normalizedPhone);
            if (existing != null) {
                // ✅ FIXED: Changed from 300000 to 3600000 (5 min to 60 min)
                signupOtpStorage.put(normalizedPhone, 
                    new SignupOtpData(null, System.currentTimeMillis() + 3600000, existing.userId, null, newResetToken));
            }
            
            return ResponseEntity.ok(Map.of(
                "verified", true,
                "resetToken", newResetToken,
                "phoneNumber", normalizedPhone,
                "message", "OTP verified. You can now reset your password."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
        }
    }
    
    @PostMapping("/send-email-otp")
    public ResponseEntity<?> sendEmailOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email not registered"));
        }
        
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        String resetToken = UUID.randomUUID().toString();
        
        // ✅ FIXED: Changed from 300000 to 3600000 (5 min to 60 min)
        signupOtpStorage.put(email, 
            new SignupOtpData(otp, System.currentTimeMillis() + 3600000, String.valueOf(userOpt.get().getId()), null, resetToken));
        
        try {
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "resetToken", resetToken,
                "email", email
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify-email-otp")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        SignupOtpData data = signupOtpStorage.get(email);
        
        if (data == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No OTP found. Please request again."));
        }
        
        if (System.currentTimeMillis() > data.expiryTime) {
            signupOtpStorage.remove(email);
            return ResponseEntity.badRequest().body(Map.of("error", "OTP expired. Please request again."));
        }
        
        if (!data.otp.equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP"));
        }
        
        String newResetToken = UUID.randomUUID().toString();
        // ✅ FIXED: Changed from 300000 to 3600000 (5 min to 60 min)
        signupOtpStorage.put(email, 
            new SignupOtpData(null, System.currentTimeMillis() + 3600000, data.userId, null, newResetToken));
        
        return ResponseEntity.ok(Map.of(
            "verified", true,
            "resetToken", newResetToken,
            "email", email,
            "message", "OTP verified. You can now reset your password."
        ));
    }
    
    @PostMapping("/reset-password-with-sms")
    public ResponseEntity<?> resetPasswordWithSms(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String newPassword = request.get("newPassword");
        String resetToken = request.get("resetToken");
        
        if (phoneNumber == null || newPassword == null || resetToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }
        
        String normalizedPhone = PhoneNumberUtil.normalizeSouthAfricanPhone(phoneNumber);
        if (normalizedPhone == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format"));
        }
        
        SignupOtpData data = signupOtpStorage.get(normalizedPhone);
        if (data == null || !resetToken.equals(data.resetToken)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired reset token"));
        }
        
        Optional<User> userOpt = userService.findByContact(normalizedPhone);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        
        User user = userOpt.get();
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(newPassword));
        userService.updateUserPassword(user);
        
        signupOtpStorage.remove(normalizedPhone);
        
        return ResponseEntity.ok(Map.of(
            "message", "Password reset successfully. You can now login with your new password.",
            "user", buildUserResponse(user),
            "hasCompletedEntry", user.isHasCompletedEntry()
        ));
    }
    
    @PostMapping("/reset-password-with-email")
    public ResponseEntity<?> resetPasswordWithEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String resetToken = request.get("resetToken");
        
        if (email == null || newPassword == null || resetToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }
        
        SignupOtpData data = signupOtpStorage.get(email);
        if (data == null || !resetToken.equals(data.resetToken)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired reset token"));
        }
        
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        
        User user = userOpt.get();
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(newPassword));
        userService.updateUserPassword(user);
        
        signupOtpStorage.remove(email);
        
        return ResponseEntity.ok(Map.of(
            "message", "Password reset successfully. You can now login with your new password.",
            "user", buildUserResponse(user),
            "hasCompletedEntry", user.isHasCompletedEntry()
        ));
    }
}