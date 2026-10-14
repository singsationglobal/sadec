package com.singsation.repository;

import com.singsation.model.CompetitionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CompetitionEntryRepository extends JpaRepository<CompetitionEntry, Long> {
    
    // Fetch entries with their vote counts
    @Query("SELECT c, COUNT(v) FROM CompetitionEntry c LEFT JOIN Vote v ON c.userid = v.artistUserid GROUP BY c.id ORDER BY c.createdAt DESC")
    List<Object[]> findAllWithVoteCounts();
}
