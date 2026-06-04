package com.singsation.controller;

import com.singsation.model.Complaint;
import com.singsation.model.User;
import com.singsation.repository.ComplaintRepository;
import com.singsation.repository.UserRepository;
import com.singsation.service.UserActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserActivityLogService userActivityLogService;

    private Map<String, Object> toComplaintDto(Complaint c) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", c.getId());
        dto.put("subject", c.getSubject());
        dto.put("message", c.getMessage());
        dto.put("status", c.getStatus());
        dto.put("adminReply", c.getAdminReply());
        dto.put("createdAt", c.getCreatedAt());
        dto.put("updatedAt", c.getUpdatedAt());
        
        User u = c.getUser();
        Map<String, Object> userDto = new HashMap<>();
        userDto.put("id", u.getId());
        userDto.put("name", u.getName());
        userDto.put("email", u.getEmail());
        userDto.put("userid", u.getUserid());
        dto.put("user", userDto);
        
        return dto;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> submitComplaint(@RequestBody Map<String, String> request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String subject = request.get("subject");
            String message = request.get("message");
            if (message == null || message.isEmpty()) {
                message = request.get("description");
            }
            
            if (subject == null || subject.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject is required", "field", "subject"));
            }
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message is required", "field", "message"));
            }
            
            Complaint complaint = new Complaint();
            complaint.setUser(user);
            complaint.setSubject(subject.trim());
            complaint.setMessage(message.trim());
            complaint.setStatus("OPEN");
            complaint.setCreatedAt(LocalDateTime.now());
            complaint.setUpdatedAt(LocalDateTime.now());
            
            Complaint saved = complaintRepository.save(complaint);
            
            // ✅ Log complaint submission activity
            HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            userActivityLogService.logActivity(user, "COMPLAINT_SUBMIT", 
                "Submitted complaint: " + subject.trim(), httpRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("status", saved.getStatus());
            response.put("message", "Complaint submitted successfully");
            response.put("complaint", toComplaintDto(saved));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to submit complaint: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getUserComplaints(@PathVariable("userId") Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User ID is required"));
            }
            
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user == null || !user.getId().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
            }
            
            List<Complaint> complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            List<Map<String, Object>> dtoList = complaints.stream()
                .map(this::toComplaintDto)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(dtoList);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getComplaintDetails(@PathVariable("id") Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(email).orElse(null);
            
            Complaint complaint = complaintRepository.findById(id).orElse(null);
            if (complaint == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (currentUser == null || !currentUser.getId().equals(complaint.getUser().getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
            }
            
            return ResponseEntity.ok(toComplaintDto(complaint));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}