package com.singsation.controller;

import com.singsation.service.AdminPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/payments")
public class AdminPaymentController {

    @Autowired
    private AdminPaymentService adminPaymentService;

    @GetMapping
    public ResponseEntity<?> getAllPayments(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        var payments = adminPaymentService.getAllPayments(PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("payments", payments.getContent());
        response.put("totalPages", payments.getTotalPages());
        response.put("totalElements", payments.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable @NonNull Long id) {
        try {
            return ResponseEntity.ok(adminPaymentService.getPaymentById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}