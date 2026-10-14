package com.singsation.controller;

import com.singsation.service.AdminVotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin/voting")
public class AdminVotingController {

    @Autowired
    private AdminVotingService adminVotingService;

    // Get total votes per artist
    @GetMapping("/votes-by-artist")
    public ResponseEntity<?> getVotesByArtist() {
        return ResponseEntity.ok(adminVotingService.getTotalVotesByArtist());
    }

    // Get detailed list of voters for a specific artist
    @GetMapping("/voters/{artistUserid}")
    public ResponseEntity<?> getVotersForArtist(@PathVariable String artistUserid) {
        return ResponseEntity.ok(adminVotingService.getVotersForArtist(artistUserid));
    }

    // Get coin stats for a specific user
    @GetMapping("/user-coins/{userId}")
    public ResponseEntity<?> getUserCoinStats(@PathVariable Long userId) {
        return ResponseEntity.ok(adminVotingService.getUserCoinStats(userId));
    }

    // Get overall platform stats
    @GetMapping("/platform-stats")
    public ResponseEntity<?> getPlatformStats() {
        return ResponseEntity.ok(adminVotingService.getPlatformStats());
    }
}
