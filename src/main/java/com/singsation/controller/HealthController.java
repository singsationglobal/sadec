package com.singsation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    /**
     * Render's health checker hits `/` repeatedly. Returning a small JSON
     * banner here keeps the API logs clean and gives a friendly "I'm alive"
     * response for anyone who visits the API root in a browser.
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "Singsation SADEC API");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}
