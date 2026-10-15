package com.singsation.controller;

import com.singsation.model.SplashScreen;
import com.singsation.service.SplashScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SplashScreenController {

    @Autowired
    private SplashScreenService splashScreenService;

    // PUBLIC ENDPOINT - This is what the user app calls
    @GetMapping("/splash-screen")
    public ResponseEntity<?> getPublicSplashScreen() {
        try {
            SplashScreen activeSplash = splashScreenService.getActiveSplashScreen();
            if (activeSplash != null && activeSplash.getImageUrl() != null) {
                return ResponseEntity.ok(Map.of("imageUrl", activeSplash.getImageUrl()));
            }
            return ResponseEntity.ok(Map.of("imageUrl", ""));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("imageUrl", ""));
        }
    }

    @PostMapping("/admin/splash-screen/upload")
    public ResponseEntity<?> uploadSplashScreen(@RequestParam("image") MultipartFile imageFile) {
        try {
            SplashScreen splash = splashScreenService.uploadSplashScreen(imageFile);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Uploaded");
            response.put("id", splash.getId());
            response.put("imageUrl", splash.getImageUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/splash-screen/all")
    public ResponseEntity<?> getAllSplashScreens() {
        try {
            List<SplashScreen> list = splashScreenService.getAllActiveSplashScreens();
            List<Map<String, Object>> response = list.stream().map(s -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", s.getId());
                m.put("imageUrl", s.getImageUrl());
                m.put("active", s.getActive());
                return m;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/splash-screen/deleted")
    public ResponseEntity<?> getDeletedSplashScreens() {
        try {
            List<SplashScreen> list = splashScreenService.getDeletedSplashScreens();
            List<Map<String, Object>> response = list.stream().map(s -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", s.getId());
                m.put("imageUrl", s.getImageUrl());
                m.put("deletedAt", s.getDeletedAt());
                return m;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/splash-screen/{id}")
    public ResponseEntity<?> deleteSplashScreen(@PathVariable Long id) {
        try {
            splashScreenService.moveToBin(id);
            return ResponseEntity.ok(Map.of("message", "Moved to bin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/splash-screen/{id}/permanent")
    public ResponseEntity<?> permanentDelete(@PathVariable Long id) {
        try {
            splashScreenService.permanentDelete(id);
            return ResponseEntity.ok(Map.of("message", "Permanently deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/admin/splash-screen/{id}/activate")
    public ResponseEntity<?> activateSplashScreen(@PathVariable Long id) {
        try {
            SplashScreen s = splashScreenService.activateSplashScreen(id);
            return ResponseEntity.ok(Map.of("message", "Activated", "id", s.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
