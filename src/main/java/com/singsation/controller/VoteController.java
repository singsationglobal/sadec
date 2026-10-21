package com.singsation.controller;

import com.singsation.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.singsation.repository.UserRepository;
import com.singsation.model.User;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VotingService votingService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/vote")
    public ResponseEntity<?> castVote(@RequestBody Map<String, Object> payload) {
        try {
            // 1. SECURITY FIX: Get userId from the JWT token, NOT the request body
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }
            String email = ((UserDetails) auth.getPrincipal()).getUsername();
            User voter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Long voterUserId = voter.getId();

            // 2. Get the entry ID, coin cost (1, 2, 3), and vote type
            if (!payload.containsKey("entryId") || !payload.containsKey("cost")) {
                return ResponseEntity.badRequest().body(Map.of("error", "entryId and cost are required"));
            }

            Long entryId = Long.valueOf(payload.get("entryId").toString());
            int cost = Integer.parseInt(payload.get("cost").toString());
            
            // Optional: Default to COIN, but can pass "ROSE" or "UNIVERSE"
            String voteType = payload.containsKey("voteType") ? payload.get("voteType").toString() : "COIN";

            if (cost <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cost must be greater than 0"));
            }

            votingService.castVote(voterUserId, entryId, cost, voteType);
            return ResponseEntity.ok(Map.of("message", "Vote cast successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
