package com.singsation.service;

import com.singsation.model.SplashScreen;
import com.singsation.repository.SplashScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SplashScreenService {
    @Autowired
    private SplashScreenRepository splashScreenRepository;

    @Autowired
    private OracleStorageService oracleStorageService;

    /**
     * Upload new splash screen - deactivates previous one
     */
    public SplashScreen uploadSplashScreen(MultipartFile imageFile) throws IOException {
        // Upload to Oracle bucket
        String imageUrl = oracleStorageService.uploadSplashScreen(imageFile);
        
        // Deactivate all existing NON-DELETED splash screens
        List<SplashScreen> existing = splashScreenRepository.findByIsDeletedFalse();
        for (SplashScreen s : existing) {
            s.setActive(false);
            splashScreenRepository.save(s);
        }
        
        // Create new active splash screen
        SplashScreen splash = new SplashScreen();
        splash.setImageUrl(imageUrl);
        splash.setActive(true);
        splash.setDeleted(false);
        return splashScreenRepository.save(splash);
    }

    /**
     * Get the currently active splash screen
     */
    public Optional<SplashScreen> getActiveSplashScreen() {
        return splashScreenRepository.findByActiveTrueAndIsDeletedFalse();
    }

    /**
     * Get all splash screens (for history) - excludes deleted ones
     */
    public List<SplashScreen> getAllSplashScreens() {
        return splashScreenRepository.findByIsDeletedFalse();
    }

    /**
     * Delete a splash screen by ID (Soft Delete)
     */
    public void deleteSplashScreen(Long id) {
        SplashScreen splash = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        splash.setDeleted(true);
        splash.setDeletedAt(LocalDateTime.now());
        splash.setActive(false);
        splashScreenRepository.save(splash);
    }

    /**
     * Set a specific splash screen as active
     */
    public SplashScreen setActiveSplashScreen(Long id) {
        SplashScreen selected = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
            
        if (selected.isDeleted()) {
            throw new RuntimeException("Cannot activate a deleted splash screen");
        }

        // Deactivate all non-deleted
        List<SplashScreen> all = splashScreenRepository.findByIsDeletedFalse();
        for (SplashScreen s : all) {
            s.setActive(false);
            splashScreenRepository.save(s);
        }
        
        // Activate selected
        selected.setActive(true);
        return splashScreenRepository.save(selected);
    }
}
