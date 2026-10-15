package com.singsation.controller;

import com.singsation.model.SplashScreen;
import com.singsation.service.SplashScreenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SplashScreenController {

    @Autowired
    private SplashScreenService splashScreenService;
    private static final Logger logger = LoggerFactory.getLogger(SplashScreenController.class);

    @GetMapping("/splash-screen")
    public ResponseEntity<?> getPublicSplashScreen() {
        try {
            Optional<SplashScreen> splash = splashScreenService.getActiveSplashScreen();
            if (splash.isPresent()) {
                return ResponseEntity.ok(Map.of("imageUrl", splash.get().getImageUrl()));
            }
            return ResponseEntity.ok(Map.of("imageUrl", (String) null));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("imageUrl", (String) null));
        }
    }

    @PostMapping("/admin/splash-screen/upload")
    public ResponseEntity<?> uploadSplashScreen(@RequestParam("image") MultipartFile imageFile) {
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No image file provided"));
            }
            SplashScreen splash = splashScreenService.uploadSplashScreen(imageFile);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Splash screen uploaded successfully");
            response.put("imageUrl", splash.getImageUrl());
            response.put("id", splash.getId());
            response.put("active", splash.getActive());
            response.put("createdAt", splash.getCreatedAt());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/splash-screen/all")
    public ResponseEntity<?> getAllSplashScreens() {
        try {
            List<SplashScreen> splashScreens = splashScreenService.getAllSplashScreens();
            List<Map<String, Object>> safeResponse = splashScreens.stream().map(screen -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", screen.getId());
                item.put("imageUrl", screen.getImageUrl());
                item.put("active", screen.getActive());
                item.put("createdAt", screen.getCreatedAt());
                return item;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(safeResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve splash screens: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/splash-screen/deleted")
    public ResponseEntity<?> getDeletedSplashScreens() {
        try {
            List<SplashScreen> deletedScreens = splashScreenService.getDeletedSplashScreens();
            List<Map<String, Object>> safeResponse = deletedScreens.stream().map(screen -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", screen.getId());
                item.put("imageUrl", screen.getImageUrl());
                item.put("active", screen.getActive());
                item.put("createdAt", screen.getCreatedAt());
                item.put("deletedAt", screen.getDeletedAt());
                return item;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(safeResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve deleted splash screens: " + e.getMessage()));
        }
    }

    @DeleteMapping("/admin/splash-screen/{id}")
    public ResponseEntity<?> deleteSplashScreen(@PathVariable Long id) {
        try {
            splashScreenService.deleteSplashScreen(id);
            return ResponseEntity.ok(Map.of("message", "Moved to Recycle Bin", "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to move to bin: " + e.getMessage()));
        }
    }

    @DeleteMapping("/admin/splash-screen/{id}/permanent")
    public ResponseEntity<?> permanentDeleteSplashScreen(@PathVariable Long id) {
        try {
            splashScreenService.permanentDelete(id);
            return ResponseEntity.ok(Map.of("message", "Permanently deleted", "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to permanently delete: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/splash-screen/{id}/activate")
    public ResponseEntity<?> activateSplashScreen(@PathVariable Long id) {
        try {
            SplashScreen splash = splashScreenService.setActiveSplashScreen(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Splash screen activated successfully");
            response.put("imageUrl", splash.getImageUrl());
            response.put("id", splash.getId());
            response.put("active", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to activate splash screen: " + e.getMessage()));
        }
    }
}
