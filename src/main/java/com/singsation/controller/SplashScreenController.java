package com.singsation.controller;

import com.singsation.model.SplashScreen;
import com.singsation.service.SplashScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/splash-screen")
public class SplashScreenController {

    @Autowired
    private SplashScreenService splashScreenService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSplashScreens() {
        List<SplashScreen> screens = splashScreenService.getAllSplashScreens();
        return ResponseEntity.ok(screens);
    }

    @GetMapping
    public ResponseEntity<?> getActiveSplashScreen() {
        SplashScreen screen = splashScreenService.getActiveSplashScreen();
        if (screen != null && screen.getImageUrl() != null && !screen.getImageUrl().isEmpty()) {
            return ResponseEntity.ok(Map.of("imageUrl", screen.getImageUrl()));
        }
        return ResponseEntity.ok(Map.of("imageUrl", "https://objectstorage.af-johannesburg-1.oraclecloud.com/n/axcbefxpjvzm/b/karaokeimages/o/Splashscreensplash.jpg"));
    }

    // ✅ THIS IS THE RECYCLE BIN ENDPOINT (It is already in the correct spot for you)
    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedSplashScreens() {
        List<SplashScreen> screens = splashScreenService.getDeletedSplashScreens();
        return ResponseEntity.ok(screens);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadSplashScreen(@RequestParam("image") MultipartFile file) {
        try {
            // NOTE: If you had your original Oracle Cloud upload code here before, 
            // you can paste it back in this exact spot. This placeholder just prevents the app from crashing.
            return ResponseEntity.badRequest().body(Map.of("error", "Please add your original upload code here"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload: " + e.getMessage()));
        }
    }

    @PostMapping("/url")
    public ResponseEntity<?> saveSplashScreenUrl(@RequestBody Map<String, String> request) {
        try {
            String imageUrl = request.get("imageUrl");
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Image URL is required"));
            }
            SplashScreen screen = new SplashScreen();
            screen.setImageUrl(imageUrl);
            splashScreenService.createSplashScreen(screen);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl, "message", "Saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateSplashScreen(@PathVariable Long id) {
        try {
            splashScreenService.activateSplashScreen(id);
            return ResponseEntity.ok(Map.of("message", "Activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/bin")
    public ResponseEntity<?> moveToBin(@PathVariable Long id) {
        try {
            splashScreenService.moveToBin(id);
            return ResponseEntity.ok(Map.of("message", "Moved to bin"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to move to bin"));
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreFromBin(@PathVariable Long id) {
        try {
            splashScreenService.restoreFromBin(id);
            return ResponseEntity.ok(Map.of("message", "Restored successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> permanentDelete(@PathVariable Long id) {
        try {
            splashScreenService.permanentDelete(id);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
