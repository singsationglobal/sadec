package com.singsation.controller;

import com.singsation.model.Admin;
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
    public ResponseEntity<List<WinnerAnnouncement>> getActiveWinners() {
        return ResponseEntity.ok(adminWinnerService.getActiveWinners());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<WinnerAnnouncement>> getDeletedWinners() {
        return ResponseEntity.ok(adminWinnerService.getDeletedWinners());
    }

    @PostMapping("/announce")
    public ResponseEntity<?> announceWinner(@RequestBody Map<String, Object> payload) {
        try {
            WinnerAnnouncement announcement = new WinnerAnnouncement();
            announcement.setCategory(payload.getOrDefault("category", "ADULTS").toString());
            announcement.setWinnerName(payload.getOrDefault("winnerName", "Unknown").toString());
            announcement.setWinnerUserid(payload.getOrDefault("winnerUserid", "Unknown").toString());
            if (payload.get("winnerAge") != null) {
                announcement.setWinnerAge(Integer.parseInt(payload.get("winnerAge").toString()));
            }
            announcement.setProvince(payload.get("province") != null ? payload.get("province").toString() : null);
            announcement.setMessage(payload.get("message") != null ? payload.get("message").toString() : null);
            
            Admin admin = new Admin();
            admin.setId(1L);
            announcement.setAnnouncedBy(admin);

            return ResponseEntity.ok(adminWinnerService.announceWinner(announcement));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to announce: " + e.getMessage()));
        }
    }

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
