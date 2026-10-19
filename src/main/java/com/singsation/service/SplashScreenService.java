package com.singsation.service;

import com.singsation.model.SplashScreen;
import com.singsation.repository.SplashScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private GoogleCloudStorageService googleCloudStorageService;

    @Value("${cloud.provider:oracle}")
    private String defaultCloudProvider;

    public SplashScreen uploadSplashScreen(MultipartFile imageFile, String cloudProvider) throws IOException {
        // Use the provider passed from the Flutter app, or fallback to default
        String provider = (cloudProvider != null && !cloudProvider.isEmpty()) ? cloudProvider : defaultCloudProvider;
        
        String imageUrl;
        if ("google".equalsIgnoreCase(provider)) {
            imageUrl = googleCloudStorageService.uploadSplashScreen(imageFile);
        } else {
            imageUrl = oracleStorageService.uploadSplashScreen(imageFile);
        }

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

    public Optional<SplashScreen> getActiveSplashScreen() {
        return splashScreenRepository.findByActiveTrue();
    }

    public List<SplashScreen> getAllSplashScreens() {
        return splashScreenRepository.findAllNonDeleted();
    }

    public void deleteSplashScreen(Long id) {
        SplashScreen screen = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        screen.setDeleted(true);
        screen.setDeletedAt(LocalDateTime.now());
        splashScreenRepository.save(screen);
    }

    public SplashScreen setActiveSplashScreen(Long id) {
        List<SplashScreen> all = splashScreenRepository.findAll();
        for (SplashScreen s : all) {
            s.setActive(false);
            splashScreenRepository.save(s);
        }
        SplashScreen selected = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        selected.setActive(true);
        return splashScreenRepository.save(selected);
    }

    public List<SplashScreen> getDeletedSplashScreens() {
        return splashScreenRepository.findAllDeleted();
    }

    public void restoreFromBin(Long id) {
        SplashScreen screen = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        screen.setDeleted(false);
        screen.setDeletedAt(null);
        splashScreenRepository.save(screen);
    }

    public void permanentDelete(Long id) {
        splashScreenRepository.deleteById(id);
    }
}
