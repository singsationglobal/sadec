package com.singsation.controller;

import com.singsation.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VotingService votingService;

    @PostMapping("/vote")
    public ResponseEntity<?> castVote(@RequestBody Map<String, Object> payload) {
        try {
            Long voterUserId = Long.valueOf(payload.get("userId").toString());
            String artistUserid = payload.get("artistUserId").toString();
            int cost = Integer.parseInt(payload.get("cost").toString());

            votingService.castVote(voterUserId, artistUserid, cost);
            return ResponseEntity.ok(Map.of("message", "Vote cast successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
