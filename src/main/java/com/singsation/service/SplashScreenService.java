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

    public SplashScreen uploadSplashScreen(MultipartFile imageFile) throws IOException {
        String imageUrl = oracleStorageService.uploadSplashScreen(imageFile);
        
        List<SplashScreen> existing = splashScreenRepository.findAll();
        for (SplashScreen s : existing) {
            s.setActive(false);
            s.setIsActive(false);
            splashScreenRepository.save(s);
        }
        
        SplashScreen splash = new SplashScreen();
        splash.setImageUrl(imageUrl);
        splash.setActive(true);
        splash.setIsActive(true);
        splash.setDeleted(false);
        return splashScreenRepository.save(splash);
    }

    public Optional<SplashScreen> getActiveSplashScreen() {
        return splashScreenRepository.findByActiveTrue();
    }

    public List<SplashScreen> getAllSplashScreens() {
        return splashScreenRepository.findAll();
    }

    public void deleteSplashScreen(Long id) {
        SplashScreen splash = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        splash.setDeleted(true);
        splash.setDeletedAt(LocalDateTime.now());
        splash.setActive(false);
        splash.setIsActive(false);
        splashScreenRepository.save(splash);
    }

    public SplashScreen setActiveSplashScreen(Long id) {
        List<SplashScreen> all = splashScreenRepository.findAll();
        for (SplashScreen s : all) {
            s.setActive(false);
            s.setIsActive(false);
            splashScreenRepository.save(s);
        }
        
        SplashScreen selected = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        selected.setActive(true);
        selected.setIsActive(true);
        return splashScreenRepository.save(selected);
    }
}
