package com.singsation.controller;

import com.singsation.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/yoco")
public class YocoCoinController {

    @Autowired
    private VotingService votingService;

    @PostMapping("/create-coin-checkout")
    public ResponseEntity<?> createCoinCheckout(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            int amountInCents = Integer.parseInt(payload.get("amount").toString());
            int coinAmount = Integer.parseInt(payload.get("coinAmount").toString());

            // TODO: Integrate your actual Yoco API call here to generate a redirectUrl.
            // For now, we return a mock URL. Replace this with your real Yoco checkout logic.
            String mockRedirectUrl = "https://pay.yoco.com/checkout/mock-success?userId=" + userId + "&coins=" + coinAmount;

            return ResponseEntity.ok(Map.of("redirectUrl", mockRedirectUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // This is the webhook Yoco calls when payment succeeds
    @PostMapping("/coin-payment-success")
    public ResponseEntity<?> handleCoinPaymentSuccess(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            int coinAmount = Integer.parseInt(payload.get("coinAmount").toString());
            
            votingService.addCoins(userId, coinAmount);
            return ResponseEntity.ok(Map.of("message", "Coins added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
