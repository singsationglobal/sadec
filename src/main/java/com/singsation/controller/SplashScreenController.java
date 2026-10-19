package com.singsation.controller;

import com.singsation.model.SplashScreen;
import com.singsation.service.SplashScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
        // Fallback image so the box is never empty
        return ResponseEntity.ok(Map.of("imageUrl", "https://objectstorage.af-johannesburg-1.oraclecloud.com/n/axcbefxpjvzm/b/karaokeimages/o/Splashscreensplash.jpg"));
    }

    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedSplashScreens() {
        List<SplashScreen> screens = splashScreenService.getDeletedSplashScreens();
        return ResponseEntity.ok(screens);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadSplashScreen(@RequestParam("image") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No file provided"));
            }

            // ==========================================================
            // OPTION 1: If you have your original Oracle Cloud upload code, 
            // paste it here and return the cloud URL.
            // Example: String cloudUrl = yourOracleService.upload(file);
            // ==========================================================

            // OPTION 2: Fallback local file save (Works immediately to prevent 400 errors)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;
            
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Note: For production on Render, you should ideally use Option 1 (Oracle Cloud). 
            // This local path is a fallback to ensure the upload succeeds and doesn't break the app.
            String fileUrl = "/uploads/" + filename; 

            SplashScreen screen = new SplashScreen();
            screen.setImageUrl(fileUrl);
            screen.setActive(false);
            screen.setIsActive(false);
            splashScreenService.createSplashScreen(screen);

            return ResponseEntity.ok(Map.of("imageUrl", fileUrl, "message", "Uploaded successfully"));

        } catch (Exception e) {
            e.printStackTrace();
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
