package com.singsation.controller;

import com.singsation.model.Complaint;
import com.singsation.service.AdminComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/complaints")
public class AdminComplaintController {

    @Autowired
    private AdminComplaintService complaintService;

    @GetMapping
    public ResponseEntity<?> getComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Complaint> complaints = complaintService.getComplaints(pageable, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("complaints", complaints.getContent());
        response.put("totalPages", complaints.getTotalPages());
        response.put("totalElements", complaints.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    // ✅ NEW: Get soft-deleted complaints (The Bin)
    @GetMapping("/bin")
    public ResponseEntity<?> getDeletedComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Complaint> complaints = complaintService.getDeletedComplaints(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("complaints", complaints.getContent());
        response.put("totalPages", complaints.getTotalPages());
        response.put("totalElements", complaints.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<?> replyToComplaint(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String adminReply = request.get("adminReply");
        String status = request.get("status");
        Complaint updated = complaintService.replyToComplaint(id, adminReply, status);
        return ResponseEntity.ok(updated);
    }

    // ✅ NEW: Soft Delete (Moves to Bin)
    @PostMapping("/{id}/bin")
    public ResponseEntity<?> moveToBin(@PathVariable Long id) {
        try {
            complaintService.moveToBin(id);
            return ResponseEntity.ok(Map.of("message", "Moved to bin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: Restore from Bin
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreFromBin(@PathVariable Long id) {
        try {
            complaintService.restoreFromBin(id);
            return ResponseEntity.ok(Map.of("message", "Restored successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: Permanent Delete (Requires confirmation on frontend)
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDelete(@PathVariable Long id) {
        try {
            complaintService.permanentDelete(id);
            return ResponseEntity.ok(Map.of("message", "Permanently deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
