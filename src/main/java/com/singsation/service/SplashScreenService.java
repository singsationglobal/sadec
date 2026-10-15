package com.singsation.service;

import com.singsation.model.SplashScreen;
import com.singsation.repository.SplashScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public SplashScreen uploadSplashScreen(MultipartFile imageFile) throws IOException {
        String imageUrl = oracleStorageService.uploadSplashScreen(imageFile);
        
        // ORIGINAL WORKING LOGIC: Deactivate all existing
        List<SplashScreen> existing = splashScreenRepository.findAll();
        for (SplashScreen s : existing) {
            s.setActive(false);
            splashScreenRepository.save(s);
        }
        
        SplashScreen splash = new SplashScreen();
        splash.setImageUrl(imageUrl);
        splash.setActive(true);
        splash.setIsDeleted(false);
        return splashScreenRepository.save(splash);
    }

    // ORIGINAL WORKING LOGIC
    public Optional<SplashScreen> getActiveSplashScreen() {
        return splashScreenRepository.findByActiveTrue();
    }

    public List<SplashScreen> getAllSplashScreens() {
        return splashScreenRepository.findByIsDeletedFalse();
    }

    public List<SplashScreen> getDeletedSplashScreens() {
        return splashScreenRepository.findByIsDeletedTrue();
    }

    @Transactional
    public void moveToBin(Long id) {
        SplashScreen screen = splashScreenRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        screen.setIsDeleted(true);
        screen.setDeletedAt(LocalDateTime.now());
        splashScreenRepository.save(screen);
    }

    @Transactional
    public void permanentDelete(Long id) {
        splashScreenRepository.deleteById(id);
    }

    // ORIGINAL WORKING LOGIC for activation
    @Transactional
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
        selected.setIsDeleted(false);
        
        return splashScreenRepository.save(selected);
    }
}
