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
import java.util.stream.Collectors;

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
     * Get all splash screens (for history) - EXCLUDES deleted ones
     */
    public List<SplashScreen> getAllSplashScreens() {
        return splashScreenRepository.findAllNonDeleted();
    }
    
    /**
     * Delete a splash screen by ID - MOVED TO BIN (soft delete)
     */
    public void deleteSplashScreen(Long id) {
        SplashScreen screen = splashScreenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Splash screen not found"));
        screen.setDeleted(true);
        screen.setDeletedAt(LocalDateTime.now());
        splashScreenRepository.save(screen);
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
    
    // ✅ ADDED: Recycle bin methods
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
