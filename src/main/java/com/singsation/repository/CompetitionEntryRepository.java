package com.singsation.repository;

import com.singsation.model.CompetitionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CompetitionEntryRepository extends JpaRepository<CompetitionEntry, Long> {

    @Query("SELECT c, COUNT(v) FROM CompetitionEntry c LEFT JOIN Vote v ON c.userid = v.artistUserid GROUP BY c.id ORDER BY c.createdAt DESC")
    List<Object[]> findAllWithVoteCounts();

    // ✅ NEW: fetch the latest entry for a user (used by /api/competitions/my-entry)
    Optional<CompetitionEntry> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
