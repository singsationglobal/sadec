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
    
    // ✅ FIXED: Check the 'active' column (not is_active) to match your database
    @Query("SELECT s FROM SplashScreen s WHERE s.active = true AND s.isDeleted = false")
    Optional<SplashScreen> findActiveScreen();

    // ✅ Return all non-deleted screens for the admin panel
    @Query("SELECT s FROM SplashScreen s WHERE s.isDeleted = false ORDER BY s.id DESC")
    List<SplashScreen> findAllNonDeleted();

    List<SplashScreen> findByIsDeletedTrue();

    @Modifying
    @Transactional
    @Query("UPDATE SplashScreen s SET s.active = false, s.isActive = false")
    void deactivateAll();
}
