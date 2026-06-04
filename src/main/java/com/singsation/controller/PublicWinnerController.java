package com.singsation.controller;

import com.singsation.service.AdminWinnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/winners")
public class PublicWinnerController {

    @Autowired
    private AdminWinnerService adminWinnerService;

    /**
     * Public endpoint for karaoke app to fetch active winners
     * No authentication required
     */
    @GetMapping("/active")
    public ResponseEntity<?> getPublicActiveWinners() {
        return ResponseEntity.ok(adminWinnerService.getActiveWinners());
    }
}