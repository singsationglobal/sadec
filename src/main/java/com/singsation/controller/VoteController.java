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

    /**
     * Cast a vote for a competition entry using a gift instrument.
     *
     * Request body:
     *   { "entryId": 123, "giftType": "DRUMS" }
     *
     * The user is identified from the JWT — never trust a userId in the body.
     * The cost is resolved server-side from GiftType. Client cannot spoof it.
     */
    @PostMapping("/vote")
    public ResponseEntity<?> castVote(@RequestBody Map<String, Object> payload) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }
            String email = ((UserDetails) auth.getPrincipal()).getUsername();
            User voter = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long voterUserId = voter.getId();

            if (!payload.containsKey("entryId") || payload.get("entryId") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "entryId is required"));
            }
            if (!payload.containsKey("giftType") || payload.get("giftType") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "giftType is required"));
            }

            Long entryId = Long.valueOf(payload.get("entryId").toString());
            String giftType = payload.get("giftType").toString();

            votingService.castGiftVote(voterUserId, entryId, giftType);

            // Return the new balance so the app updates instantly
            int newBalance = votingService.getCoinBalance(voterUserId);
            return ResponseEntity.ok(Map.of(
                    "message", "Vote cast successfully",
                    "newBalance", newBalance
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
