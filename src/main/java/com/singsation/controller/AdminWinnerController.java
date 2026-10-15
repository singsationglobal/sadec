package com.singsation.controller;

import com.singsation.model.WinnerAnnouncement;
import com.singsation.service.AdminWinnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/winners")
public class AdminWinnerController {

    @Autowired
    private AdminWinnerService adminWinnerService;

    @GetMapping("/active")
    public ResponseEntity<?> getActiveWinners() {
        return ResponseEntity.ok(adminWinnerService.getActiveWinners());
    }

    // ✅ NEW: Endpoint for the Recycle Bin
    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedWinners() {
        return ResponseEntity.ok(adminWinnerService.getDeletedWinners());
    }

    @PostMapping("/announce")
    public ResponseEntity<?> announceWinner(@RequestBody WinnerAnnouncement announcement) {
        try {
            return ResponseEntity.ok(adminWinnerService.announceWinner(announcement));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWinner(@PathVariable Long id, @RequestBody WinnerAnnouncement updatedData) {
        try {
            return ResponseEntity.ok(adminWinnerService.updateWinner(id, updatedData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: One-click move to bin (No confirmation needed here)
    @PostMapping("/{id}/bin")
    public ResponseEntity<?> moveToBin(@PathVariable Long id) {
        try {
            adminWinnerService.moveToBin(id);
            return ResponseEntity.ok(Map.of("message", "Moved to Recycle Bin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreFromBin(@PathVariable Long id) {
        try {
            adminWinnerService.restoreFromBin(id);
            return ResponseEntity.ok(Map.of("message", "Restored successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDelete(@PathVariable Long id) {
        try {
            adminWinnerService.permanentDelete(id);
            return ResponseEntity.ok(Map.of("message", "Permanently deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
