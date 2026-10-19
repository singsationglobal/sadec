package com.singsation.repository;

import com.singsation.model.CompetitionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompetitionRepository extends JpaRepository<CompetitionEntry, Long> {
    
    @Query("SELECT c FROM CompetitionEntry c ORDER BY c.votes DESC")
    List<CompetitionEntry> findAllOrderByVotesDesc();

    @Query("SELECT c FROM CompetitionEntry c WHERE c.category = :category ORDER BY c.votes DESC")
    List<CompetitionEntry> findByCategoryOrderByVotesDesc(String category);
}
