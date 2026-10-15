package com.singsation.repository;

import com.singsation.model.SplashScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SplashScreenRepository extends JpaRepository<SplashScreen, Long> {
    // Matches the fields 'active' and 'isDeleted' in the Entity
    SplashScreen findByActiveTrueAndIsDeletedFalse();
    List<SplashScreen> findByIsDeletedFalse();
    List<SplashScreen> findByIsDeletedTrue();
}
