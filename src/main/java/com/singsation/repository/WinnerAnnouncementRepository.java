package com.singsation.repository;

import com.singsation.model.WinnerAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WinnerAnnouncementRepository extends JpaRepository<WinnerAnnouncement, Long> {
    
    // ✅ UPDATED: Only find active winners that are NOT deleted
    Optional<WinnerAnnouncement> findByCategoryAndIsActiveTrueAndIsDeletedFalse(String category);
    List<WinnerAnnouncement> findByIsActiveTrueAndIsDeletedFalseOrderByAnnouncedAtDesc();
    List<WinnerAnnouncement> findAllByOrderByAnnouncedAtDesc();
}
