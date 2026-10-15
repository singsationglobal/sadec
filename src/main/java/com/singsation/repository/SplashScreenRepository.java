package com.singsation.repository;

import com.singsation.model.SplashScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SplashScreenRepository extends JpaRepository<SplashScreen, Long> {
    // Original working method
    Optional<SplashScreen> findByActiveTrue();
    
    // For the recycle bin
    List<SplashScreen> findByIsDeletedTrue();
    List<SplashScreen> findByIsDeletedFalse();
}
