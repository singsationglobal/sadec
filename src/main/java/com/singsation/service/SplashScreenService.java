package com.singsation.service;

import com.singsation.model.SplashScreen;
import com.singsation.repository.SplashScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SplashScreenService {

    @Autowired
    private SplashScreenRepository splashScreenRepository;

    public List<SplashScreen> getAllSplashScreens() {
        return splashScreenRepository.findAllActiveScreens();
    }

    public SplashScreen getSplashScreenById(Long id) {
        return splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
    }

    public SplashScreen getActiveSplashScreen() {
        return splashScreenRepository.findByIsActiveTrueAndIsDeletedFalse()
            .orElse(null);
    }

    @Transactional
    public SplashScreen createSplashScreen(SplashScreen splashScreen) {
        splashScreen.setDeleted(false);
        splashScreen.setActive(false); // New uploads start as inactive
        return splashScreenRepository.save(splashScreen);
    }

    // ✅ FIXED: This ensures ONLY ONE screen is active at a time
    @Transactional
    public void activateSplashScreen(Long id) {
        // Step 1: Deactivate ALL screens first
        splashScreenRepository.deactivateAll();
        
        // Step 2: Activate the selected screen
        SplashScreen screen = getSplashScreenById(id);
        screen.setActive(true);
        screen.setIsActive(true);
        splashScreenRepository.save(screen);
    }

    @Transactional
    public void moveToBin(Long id) {
        SplashScreen screen = getSplashScreenById(id);
        screen.setDeleted(true);
        screen.setDeletedAt(LocalDateTime.now());
        splashScreenRepository.save(screen);
    }

    @Transactional
    public void restoreFromBin(Long id) {
        SplashScreen screen = getSplashScreenById(id);
        screen.setDeleted(false);
        screen.setDeletedAt(null);
        splashScreenRepository.save(screen);
    }

    @Transactional
    public void permanentDelete(Long id) {
        splashScreenRepository.deleteById(id);
    }

    public List<SplashScreen> getDeletedSplashScreens() {
        return splashScreenRepository.findByIsDeletedTrue();
    }
}
