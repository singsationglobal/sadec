package com.singsation.controller;

import com.singsation.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class CoinController {

    @Autowired
    private VotingService votingService;

    @GetMapping("/{userId}/coins")
    public ResponseEntity<?> getCoinBalance(@PathVariable Long userId) {
        try {
            int balance = votingService.getCoinBalance(userId);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
