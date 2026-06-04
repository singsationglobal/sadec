package com.singsation.repository;

import com.singsation.model.WinnerAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WinnerAnnouncementRepository extends JpaRepository<WinnerAnnouncement, Long> {
    Optional<WinnerAnnouncement> findByCategoryAndIsActiveTrue(String category);
    List<WinnerAnnouncement> findByIsActiveTrueOrderByAnnouncedAtDesc();
    List<WinnerAnnouncement> findAllByOrderByAnnouncedAtDesc();
}