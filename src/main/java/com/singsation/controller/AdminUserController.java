package com.singsation.controller;

import com.singsation.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    // ADD THIS METHOD - RESET COMPETITION ENTRY
    @PostMapping("/{id}/reset-competition")
    public ResponseEntity<?> resetCompetitionEntry(@PathVariable @NonNull Long id) {
        System.out.println("=== AdminUserController.resetCompetitionEntry CALLED for ID: " + id);
        adminUserService.resetCompetitionEntry(id);
        return ResponseEntity.ok(Map.of("message", "Competition entry reset successfully"));
    }
}