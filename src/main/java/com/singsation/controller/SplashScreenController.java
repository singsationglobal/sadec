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

    // PUBLIC ENDPOINT - for Flutter app
    @GetMapping("/splash-screen")
    public ResponseEntity<?> getPublicSplashScreen() {
        try {
            Optional<SplashScreen> splash = splashScreenService.getActiveSplashScreen();
            
            if (splash != null && splash.isPresent() && splash.get() != null) {
                SplashScreen activeSplash = splash.get();
                String imageUrl = activeSplash.getImageUrl();
                
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    logger.info("Returning active splash screen for user app: {}", imageUrl);
                    return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
                }
            }
            
            logger.warn("No active splash screen found for user app");
            return ResponseEntity.ok(Map.of("imageUrl", (String) null));
        } catch (Exception e) {
            logger.error("Error getting public splash screen", e);
            return ResponseEntity.ok(Map.of("imageUrl", (String) null));
        }
    }

    // ADMIN ENDPOINTS
    @PostMapping("/admin/splash-screen/upload")
    public ResponseEntity<?> uploadSplashScreen(@RequestParam("image") MultipartFile imageFile) {
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No image file provided"));
            }
            
            SplashScreen splash = splashScreenService.uploadSplashScreen(imageFile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Splash screen uploaded successfully");
            response.put("imageUrl", splash.getImageUrl() != null ? splash.getImageUrl() : "");
            response.put("id", splash.getId());
            response.put("active", splash.isActive());
            response.put("createdAt", splash.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Failed to upload splash screen image", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during splash screen upload", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/splash-screen")
    public ResponseEntity<?> getActiveSplashScreen() {
        try {
            Optional<SplashScreen> splash = splashScreenService.getActiveSplashScreen();
            
            Map<String, Object> response = new HashMap<>();
            
            if (splash != null && splash.isPresent() && splash.get() != null) {
                SplashScreen activeSplash = splash.get();
                response.put("hasActive", true);
                response.put("imageUrl", activeSplash.getImageUrl() != null ? activeSplash.getImageUrl() : "");
                response.put("id", activeSplash.getId());
                response.put("active", activeSplash.isActive());
                response.put("createdAt", activeSplash.getCreatedAt());
            } else {
                response.put("hasActive", false);
                response.put("imageUrl", null);
                response.put("message", "No active splash screen found");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting active splash screen", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve splash screen: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/splash-screen/all")
    public ResponseEntity<?> getAllSplashScreens() {
        try {
            List<SplashScreen> splashScreens = splashScreenService.getAllSplashScreens();
            
            if (splashScreens == null) {
                return ResponseEntity.ok(List.of());
            }
            
            List<Map<String, Object>> safeResponse = splashScreens.stream()
                .map(screen -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", screen.getId());
                    item.put("imageUrl", screen.getImageUrl() != null ? screen.getImageUrl() : "");
                    item.put("active", screen.isActive());
                    item.put("createdAt", screen.getCreatedAt());
                    return item;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(safeResponse);
        } catch (Exception e) {
            logger.error("Error getting all splash screens", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve splash screens: " + e.getMessage()));
        }
    }

    @DeleteMapping("/admin/splash-screen/{id}")
    public ResponseEntity<?> deleteSplashScreen(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid splash screen ID"));
            }
            
            splashScreenService.deleteSplashScreen(id);
            return ResponseEntity.ok(Map.of(
                "message", "Splash screen deleted successfully",
                "id", id
            ));
        } catch (Exception e) {
            logger.error("Error deleting splash screen with id: {}", id, e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to delete splash screen: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/splash-screen/{id}/activate")
    public ResponseEntity<?> activateSplashScreen(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid splash screen ID"));
            }
            
            SplashScreen splash = splashScreenService.setActiveSplashScreen(id);
            
            if (splash == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Splash screen activated successfully");
            response.put("imageUrl", splash.getImageUrl() != null ? splash.getImageUrl() : "");
            response.put("id", splash.getId());
            response.put("active", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error activating splash screen with id: {}", id, e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to activate splash screen: " + e.getMessage()));
        }
    }
}