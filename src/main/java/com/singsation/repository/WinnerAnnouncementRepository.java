package com.singsation.repository;

import com.singsation.model.WinnerAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WinnerAnnouncementRepository extends JpaRepository<WinnerAnnouncement, Long> {
    List<WinnerAnnouncement> findByIsActiveTrueAndIsDeletedFalseOrderByAnnouncedAtDesc();
    List<WinnerAnnouncement> findByIsDeletedTrueOrderByDeletedAtDesc();
}
