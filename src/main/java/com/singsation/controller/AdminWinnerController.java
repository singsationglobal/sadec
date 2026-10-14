package com.singsation.controller;

import com.singsation.model.WinnerAnnouncement;
import com.singsation.service.AdminWinnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/winners")
public class AdminWinnerController {

    @Autowired
    private AdminWinnerService adminWinnerService;

    @PostMapping("/announce")
    public ResponseEntity<?> announceWinner(@RequestBody WinnerAnnouncement announcement) {
        return ResponseEntity.ok(adminWinnerService.announceWinner(announcement));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveWinners() {
        return ResponseEntity.ok(adminWinnerService.getActiveWinners());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getWinnerByCategory(@PathVariable @NonNull String category) {
        return adminWinnerService.getWinnerByCategory(category)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWinner(@PathVariable Long id, @RequestBody WinnerAnnouncement announcement) {
        try {
            WinnerAnnouncement updated = adminWinnerService.updateWinner(id, announcement);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: Soft Delete Endpoint (Matches Flutter POST request)
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> moveToRecycleBin(@PathVariable @NonNull Long id) {
        try {
            adminWinnerService.moveToRecycleBin(id);
            return ResponseEntity.ok(Map.of("message", "Winner moved to Recycle Bin"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: Permanent Delete Endpoint (Matches Flutter DELETE request)
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDelete(@PathVariable @NonNull Long id) {
        try {
            adminWinnerService.permanentDelete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Winner permanently deleted");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}
