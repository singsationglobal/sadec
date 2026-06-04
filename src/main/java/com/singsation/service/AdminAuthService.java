package com.singsation.service;

import com.singsation.model.Admin;
import com.singsation.repository.AdminRepository;
import com.singsation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminAuthService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String authenticate(String email, String rawPassword) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            
            boolean passwordMatches = passwordEncoder.matches(rawPassword, admin.getPassword());
            
            if (!passwordMatches) {
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                passwordMatches = bcrypt.matches(rawPassword, admin.getPassword());
            }
            
            if (passwordMatches && admin.isActive()) {
                admin.setLastLogin(LocalDateTime.now());
                adminRepository.save(admin);
                return jwtUtil.generateToken(admin.getEmail());
            }
        }
        return null;
    }

    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public Admin save(@NonNull Admin admin) {
        return adminRepository.save(admin);
    }
}