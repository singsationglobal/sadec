package com.singsation.controller;

import com.singsation.model.UserActivityLog;
import com.singsation.service.UserActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class UserActivityController {
    
    @Autowired
    private UserActivityLogService userActivityLogService;
    
    // Existing endpoint - keep for backward compatibility
    @GetMapping("/user-activity")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getAllActivity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<UserActivityLog> logs = userActivityLogService.getAllActivity(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs.getContent());
        response.put("totalElements", logs.getTotalElements());
        response.put("totalPages", logs.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok(response);
    }
    
    // NEW ENDPOINT - matches Flutter admin app's expectation
    @GetMapping("/logs/user-activity")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getUserActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<UserActivityLog> logs = userActivityLogService.getAllActivity(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs.getContent());
        response.put("totalElements", logs.getTotalElements());
        response.put("totalPages", logs.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user-activity/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getUserActivity(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<UserActivityLog> logs = userActivityLogService.getUserActivity(
            userId,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs.getContent());
        response.put("totalElements", logs.getTotalElements());
        response.put("totalPages", logs.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user-activity/stats/today")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getTodayStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeUsersToday", userActivityLogService.getActiveUsersToday());
        stats.put("totalLoginsToday", userActivityLogService.getTotalLoginsToday());
        stats.put("totalSongPlaysToday", userActivityLogService.getTotalSongPlaysToday());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/user-activity/by-action")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<?> getActivityByAction(
            @RequestParam String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<UserActivityLog> logs = userActivityLogService.getActivityByAction(
            action,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs.getContent());
        response.put("totalElements", logs.getTotalElements());
        response.put("totalPages", logs.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok(response);
    }
}