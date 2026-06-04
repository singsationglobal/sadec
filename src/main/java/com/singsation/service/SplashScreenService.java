package com.singsation.service;

import com.singsation.model.SplashScreen;
import com.singsation.repository.SplashScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        
        // Deactivate all existing splash screens
        List<SplashScreen> existing = splashScreenRepository.findAll();
        for (SplashScreen s : existing) {
            s.setActive(false);
            splashScreenRepository.save(s);
        }
        
        // Create new active splash screen
        SplashScreen splash = new SplashScreen();
        splash.setImageUrl(imageUrl);
        splash.setActive(true);
        
        return splashScreenRepository.save(splash);
    }

    /**
     * Get the currently active splash screen
     */
    public Optional<SplashScreen> getActiveSplashScreen() {
        return splashScreenRepository.findByActiveTrue();
    }

    /**
     * Get all splash screens (for history)
     */
    public List<SplashScreen> getAllSplashScreens() {
        return splashScreenRepository.findAll();
    }

    /**
     * Delete a splash screen by ID
     */
    public void deleteSplashScreen(Long id) {
        splashScreenRepository.deleteById(id);
    }

    /**
     * Set a specific splash screen as active
     */
    public SplashScreen setActiveSplashScreen(Long id) {
        // Deactivate all
        List<SplashScreen> all = splashScreenRepository.findAll();
        for (SplashScreen s : all) {
            s.setActive(false);
            splashScreenRepository.save(s);
        }
        
        // Activate selected
        SplashScreen selected = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        selected.setActive(true);
        
        return splashScreenRepository.save(selected);
    }
}