package com.singsation.service;

import com.singsation.model.Payment;
import com.singsation.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AdminPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Page<Payment> getAllPayments(@NonNull Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    public Payment getPaymentById(@NonNull Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    // ADD THIS METHOD FOR DASHBOARD
    public long getTotalPaymentsCount() {
        return paymentRepository.count();
    }
}