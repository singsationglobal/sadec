package com.singsation.controller;

import com.singsation.service.AdminLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {

    @Autowired
    private AdminLogService adminLogService;

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<?> getLogsByAdmin(@PathVariable Long adminId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        var logs = adminLogService.getLogsByAdmin(adminId, PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs.getContent());
        response.put("totalPages", logs.getTotalPages());
        response.put("totalElements", logs.getTotalElements());
        return ResponseEntity.ok(response);
    }
}