package com.singsation.repository;

import com.singsation.model.CompetitionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompetitionRepository extends JpaRepository<CompetitionEntry, Long> {
    List<CompetitionEntry> findByUserId(Long userId);
    List<CompetitionEntry> findByCategory(String category);
    boolean existsByUserId(Long userId);
}