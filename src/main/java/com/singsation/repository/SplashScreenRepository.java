package com.singsation.repository;
import com.singsation.model.SplashScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SplashScreenRepository extends JpaRepository<SplashScreen, Long> {
    // ✅ DEVELOPER'S ORIGINAL METHOD
    Optional<SplashScreen> findByActiveTrue();

    // ✅ ADDED: Recycle bin queries
    @Query("SELECT s FROM SplashScreen s WHERE s.isDeleted = false ORDER BY s.id DESC")
    List<SplashScreen> findAllNonDeleted();

    @Query("SELECT s FROM SplashScreen s WHERE s.isDeleted = true ORDER BY s.deletedAt DESC")
    List<SplashScreen> findAllDeleted();
}
