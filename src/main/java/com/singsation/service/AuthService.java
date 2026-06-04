package com.singsation.service;

import com.singsation.model.User;
import com.singsation.repository.UserRepository;
import com.singsation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private String normalizePhoneNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String cleaned = input.replaceAll("\\s+", "");
        if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1);
        }
        if (cleaned.startsWith("27") && cleaned.length() == 11) {
            cleaned = "0" + cleaned.substring(2);
        }
        if (!cleaned.matches("^0[0-9]{9}$")) {
            return null;
        }
        return cleaned;
    }
    
    public String authenticate(String username, String password) {
        Optional<User> userOpt;
        
        if (username != null && username.contains("@")) {
            userOpt = userRepository.findByEmail(username);
        } else {
            String normalizedPhone = normalizePhoneNumber(username);
            if (normalizedPhone == null) {
                return null;
            }
            userOpt = userRepository.findByContact(normalizedPhone);
        }
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(user.getEmail());
            }
        }
        return null;
    }
    
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserid("SING_" + System.currentTimeMillis());
        return userRepository.save(user);
    }
}