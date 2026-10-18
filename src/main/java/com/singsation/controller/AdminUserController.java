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

    // ✅ UPDATED: Calls permanentDeleteUser instead of the old deleteUser
    @DeleteMapping("/{id}")
    public ResponseEntity<?> permanentDeleteUser(@PathVariable @NonNull Long id) {
        System.out.println("=== AdminUserController.permanentDeleteUser CALLED for ID: " + id);
        adminUserService.permanentDeleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User permanently deleted"));
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

    @PostMapping("/{id}/bin")
    public ResponseEntity<?> moveToBin(@PathVariable @NonNull Long id) {
        try {
            adminUserService.softDeleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Moved to bin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreFromBin(@PathVariable @NonNull Long id) {
        try {
            adminUserService.restoreUser(id);
            return ResponseEntity.ok(Map.of("message", "Restored successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDelete(@PathVariable @NonNull Long id) {
        try {
            adminUserService.permanentDeleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Permanently deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/message")
    public ResponseEntity<?> sendMessageToUser(
            @PathVariable @NonNull Long userId,
            @RequestBody Map<String, String> request) {
        
        try {
            String messageType = request.get("messageType");
            String messageContent = request.get("messageContent");
            
            if (messageType == null || messageContent == null || messageContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "messageType and messageContent are required"));
            }
            
            adminUserService.sendMessageToUser(userId, messageType, messageContent);
            
            return ResponseEntity.ok(Map.of("message", "Message sent successfully to user"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
