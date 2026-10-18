package com.singsation.repository;

import com.singsation.model.SplashScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface SplashScreenRepository extends JpaRepository<SplashScreen, Long> {
    
    Optional<SplashScreen> findByIsActiveTrueAndIsDeletedFalse();
    
    List<SplashScreen> findByIsDeletedTrue();
    
    @Query("SELECT s FROM SplashScreen s WHERE s.isDeleted = false")
    List<SplashScreen> findAllActiveScreens();
    
    // ✅ NEW: Deactivate all splash screens
    @Modifying
    @Transactional
    @Query("UPDATE SplashScreen s SET s.active = false, s.isActive = false WHERE s.active = true OR s.isActive = true")
    void deactivateAll();
}
