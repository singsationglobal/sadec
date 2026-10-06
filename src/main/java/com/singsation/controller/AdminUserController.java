package com.singsation.controller;

import com.singsation.model.User;
import com.singsation.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        System.out.println("=== AdminUserController.getAllUsers CALLED ===");
        System.out.println("Page: " + page + ", Size: " + size);
        
        var users = adminUserService.getAllUsers(PageRequest.of(page, size));
        
        System.out.println("Users found in controller: " + users.getTotalElements());
        System.out.println("Users content size: " + users.getContent().size());
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("totalPages", users.getTotalPages());
        response.put("totalElements", users.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    // ─── 1. SEARCH ENDPOINT ───
    // CRITICAL: This MUST be placed BEFORE /{id} to prevent Spring from 
    // trying to parse the word "search" as a Long ID, which would crash the app.
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = adminUserService.searchUsers(query, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("totalPages", users.getTotalPages());
        response.put("totalElements", users.getTotalElements());
        response.put("currentPage", users.getNumber());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable @NonNull Long id) {
        System.out.println("=== AdminUserController.getUserById CALLED for ID: " + id);
        return adminUserService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @NonNull Long id) {
        System.out.println("=== AdminUserController.deleteUser CALLED for ID: " + id);
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @PostMapping("/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable @NonNull Long id) {
        System.out.println("=== AdminUserController.banUser CALLED for ID: " + id);
        adminUserService.banUser(id);
        return ResponseEntity.ok(Map.of("message", "User banned"));
    }

    @PostMapping("/{id}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable @NonNull Long id) {
        System.out.println("=== AdminUserController.unbanUser CALLED for ID: " + id);
        adminUserService.unbanUser(id);
        return ResponseEntity.ok(Map.of("message", "User unbanned"));
    }

    @PostMapping("/{id}/reset-competition")
    public ResponseEntity<?> resetCompetitionEntry(@PathVariable @NonNull Long id) {
        System.out.println("=== AdminUserController.resetCompetitionEntry CALLED for ID: " + id);
        adminUserService.resetCompetitionEntry(id);
        return ResponseEntity.ok(Map.of("message", "Competition entry reset successfully"));
    }

    // ─── 2. MESSAGE ENDPOINT ───
    @PostMapping("/{userId}/message")
    public ResponseEntity<?> sendMessageToUser(
            @PathVariable @NonNull Long userId,
            @RequestBody Map<String, String> request) {
        
        try {
            String messageType = request.getOrDefault("messageType", "General Update");
            String messageContent = request.getOrDefault("messageContent", "");
            
            User user = adminUserService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            System.out.println("=== MESSAGE SENT to User ID: " + userId + 
                               " | Type: " + messageType + 
                               " | Content: " + messageContent);
            
            return ResponseEntity.ok(Map.of("message", "Message sent successfully to " + user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
