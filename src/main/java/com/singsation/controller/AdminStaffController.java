package com.singsation.controller;

import com.singsation.model.Admin;
import com.singsation.repository.AdminRoleRepository;
import com.singsation.service.AdminStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/staff")
public class AdminStaffController {

    @Autowired
    private AdminStaffService adminStaffService;

    @Autowired
    private AdminRoleRepository adminRoleRepository;

    @GetMapping
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.ok(adminStaffService.getAllStaff());
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(adminRoleRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable @NonNull Long id) {
        return adminStaffService.getStaffById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody Admin admin,
                                          @RequestParam String roleName,
                                          @RequestParam String rawPassword) {
        return ResponseEntity.ok(adminStaffService.createStaff(admin, roleName, rawPassword));
    }

    @DeleteMapping("/{id}")
public ResponseEntity<?> deleteStaff(@PathVariable @NonNull Long id, 
                                      @RequestHeader(value = "X-Admin-Id", required = false) Long currentAdminId) {
    // If header not provided, we need to get from context
    if (currentAdminId == null) {
        // Get from security context - you may need to implement this
        // For now, we'll use a placeholder - you should implement proper extraction
        return ResponseEntity.status(400).body(Map.of("error", "Admin ID required"));
    }
    adminStaffService.deleteStaff(id, currentAdminId);
    return ResponseEntity.ok(Map.of("message", "Staff deleted"));
}
}