package com.singsation.controller;

import com.singsation.service.AdminUserService;
import com.singsation.service.AdminSongService;
import com.singsation.service.AdminPaymentService;
import com.singsation.service.AdminComplaintService;
import com.singsation.service.AdminVotingService; // ✅ Added Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private AdminUserService adminUserService;
    
    @Autowired
    private AdminSongService adminSongService;
    
    @Autowired
    private AdminPaymentService adminPaymentService;
    
    @Autowired
    private AdminComplaintService adminComplaintService;
    
    @Autowired
    private AdminVotingService adminVotingService; // ✅ Added Injection

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", adminUserService.getTotalUsersCount());
        stats.put("totalSongs", adminSongService.getTotalSongsCount());
        stats.put("totalPayments", adminPaymentService.getTotalPaymentsCount());
        stats.put("totalComplaints", adminComplaintService.getTotalComplaintsCount());
        
        // ✅ ADDED: Fetch platform stats for the vote and coin cards
        Map<String, Object> platformStats = adminVotingService.getPlatformStats();
        stats.put("totalVotesCast", platformStats.get("totalVotesCast"));
        stats.put("totalCoinsSpentPlatform", platformStats.get("totalCoinsSpentPlatform"));
        
        System.out.println("=== DASHBOARD STATS ===");
        System.out.println("Total Users: " + stats.get("totalUsers"));
        System.out.println("Total Songs: " + stats.get("totalSongs"));
        System.out.println("Total Payments: " + stats.get("totalPayments"));
        System.out.println("Total Complaints: " + stats.get("totalComplaints"));
        System.out.println("Total Votes: " + stats.get("totalVotesCast"));
        System.out.println("Total Coins Spent: " + stats.get("totalCoinsSpentPlatform"));
        
        return ResponseEntity.ok(stats);
    }
}
