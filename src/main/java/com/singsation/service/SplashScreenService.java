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

@Service
public class SplashScreenService {

    @Autowired
    private SplashScreenRepository splashScreenRepository;

    @Autowired
    private OracleStorageService oracleStorageService;

    @Transactional
    public SplashScreen uploadSplashScreen(MultipartFile imageFile) throws IOException {
        String imageUrl = oracleStorageService.uploadSplashScreen(imageFile);
        
        // Deactivate existing
        List<SplashScreen> existing = splashScreenRepository.findByIsDeletedFalse();
        for (SplashScreen s : existing) {
            s.setActive(false);
            s.setIsActive(false);
            splashScreenRepository.save(s);
        }
        
        SplashScreen splash = new SplashScreen();
        splash.setImageUrl(imageUrl);
        splash.setActive(true);
        splash.setIsActive(true);
        splash.setIsDeleted(false);
        splash.setCreatedAt(LocalDateTime.now());
        return splashScreenRepository.save(splash);
    }

    public SplashScreen getActiveSplashScreen() {
        return splashScreenRepository.findByActiveTrueAndIsDeletedFalse();
    }

    public List<SplashScreen> getAllActiveSplashScreens() {
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

    @Transactional
    public SplashScreen activateSplashScreen(Long id) {
        List<SplashScreen> all = splashScreenRepository.findByIsDeletedFalse();
        for (SplashScreen s : all) {
            s.setActive(false);
            s.setIsActive(false);
            splashScreenRepository.save(s);
        }
        SplashScreen selected = splashScreenRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        selected.setActive(true);
        selected.setIsActive(true);
        selected.setIsDeleted(false);
        return splashScreenRepository.save(selected);
    }
}
