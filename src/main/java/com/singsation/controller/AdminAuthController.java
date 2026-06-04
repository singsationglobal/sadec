package com.singsation.controller;

import com.singsation.dto.AdminLoginRequest;
import com.singsation.dto.AdminLoginResponse;
import com.singsation.model.Admin;
import com.singsation.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        String token = adminAuthService.authenticate(request.getEmail(), request.getPassword());
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        Admin admin = adminAuthService.findByEmail(request.getEmail()).orElse(null);
        return ResponseEntity.ok(new AdminLoginResponse(token, admin));
    }
}