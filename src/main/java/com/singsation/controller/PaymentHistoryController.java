package com.singsation.controller;

import com.singsation.model.Payment;
import com.singsation.model.User;
import com.singsation.repository.PaymentRepository;
import com.singsation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentHistoryController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserService userService;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String email = userDetails.getUsername();
            return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Not authenticated");
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory() {
        try {
            User user = getAuthenticatedUser();
            List<Payment> payments = paymentRepository.findByUser(user);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}