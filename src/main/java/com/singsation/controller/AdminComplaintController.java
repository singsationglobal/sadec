package com.singsation.controller;

import com.singsation.dto.ComplaintReplyRequest;
import com.singsation.service.AdminComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/complaints")
public class AdminComplaintController {

    @Autowired
    private AdminComplaintService adminComplaintService;

    @GetMapping
    public ResponseEntity<?> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        var complaints = adminComplaintService.getAllComplaints(PageRequest.of(page, size), status);
        Map<String, Object> response = new HashMap<>();
        response.put("complaints", complaints.getContent());
        response.put("totalPages", complaints.getTotalPages());
        response.put("totalElements", complaints.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable @NonNull Long id) {
        try {
            return ResponseEntity.ok(adminComplaintService.getComplaintById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<?> replyToComplaint(@PathVariable @NonNull Long id, @RequestBody ComplaintReplyRequest request) {
        return ResponseEntity.ok(adminComplaintService.replyToComplaint(id, request.getAdminReply(), request.getStatus()));
    }
}