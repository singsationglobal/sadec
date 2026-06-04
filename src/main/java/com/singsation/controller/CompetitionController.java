package com.singsation.controller;

import com.singsation.model.CompetitionEntry;
import com.singsation.service.CompetitionService;
import com.singsation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {
    
    @Autowired
    private CompetitionService competitionService;
    
    @Autowired
    private UserService userService;
    
    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String email = userDetails.getUsername();
            return userService.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Not authenticated");
    }
    
    @PostMapping("/entry")
    public ResponseEntity<?> submitEntry(@RequestBody Map<String, Object> request) {
        try {
            Long authenticatedUserId = getAuthenticatedUserId();
            
            Object userIdObj = request.get("userId");
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
            }
            
            Long requestUserId;
            try {
                requestUserId = Long.parseLong(userIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId must be a valid number"));
            }
            
            if (!authenticatedUserId.equals(requestUserId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Cannot submit entry for another user"));
            }
            
            String category = request.containsKey("category") ? request.get("category").toString() : null;
            if (category == null || category.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "category is required"));
            }
            category = category.trim().toUpperCase();
            
            String artistName = request.containsKey("artistName") ? request.get("artistName").toString() : null;
            if (artistName == null || artistName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "artistName is required"));
            }
            
            Object ageObj = request.get("age");
            if (ageObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "age is required"));
            }
            Integer age;
            try {
                age = Integer.parseInt(ageObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "age must be a valid number"));
            }
            
            String newUserid = request.containsKey("userid") ? request.get("userid").toString() : null;
            if (newUserid != null) newUserid = newUserid.trim();
            
            String contactInfo = request.containsKey("contactInfo") ? request.get("contactInfo").toString() : null;
            if (contactInfo != null) contactInfo = contactInfo.trim();
            
            String province = request.containsKey("province") ? request.get("province").toString() : null;
            if (province == null || province.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Province is required"));
            }
            province = province.trim();
            
            if ("CHILDREN".equals(category) && age > 18) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Age limit for Children category is 18"
                ));
            }
            if ("ADULTS".equals(category) && age < 18) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Minimum age for Adults category is 18"
                ));
            }
            
            CompetitionEntry entry = new CompetitionEntry();
            entry.setCategory(category);
            entry.setArtistName(artistName.trim());
            entry.setAge(age);
            entry.setUserid(newUserid);
            entry.setContactInfo(contactInfo);
            entry.setProvince(province);
            
            CompetitionEntry saved = competitionService.submitEntry(authenticatedUserId, entry);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("status", "SUCCESS");
            response.put("hasCompletedEntry", true);
            response.put("userid", newUserid);
            response.put("province", province);
            response.put("message", "Entry submitted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred. Please try again."));
        }
    }
}