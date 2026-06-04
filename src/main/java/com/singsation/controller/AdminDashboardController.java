package com.singsation.controller;

import com.singsation.service.AdminUserService;
import com.singsation.service.AdminSongService;
import com.singsation.service.AdminPaymentService;
import com.singsation.service.AdminComplaintService;
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

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", adminUserService.getTotalUsersCount());
        stats.put("totalSongs", adminSongService.getTotalSongsCount());
        stats.put("totalPayments", adminPaymentService.getTotalPaymentsCount());
        stats.put("totalComplaints", adminComplaintService.getTotalComplaintsCount());
        
        System.out.println("=== DASHBOARD STATS ===");
        System.out.println("Total Users: " + stats.get("totalUsers"));
        System.out.println("Total Songs: " + stats.get("totalSongs"));
        System.out.println("Total Payments: " + stats.get("totalPayments"));
        System.out.println("Total Complaints: " + stats.get("totalComplaints"));
        
        return ResponseEntity.ok(stats);
    }
}