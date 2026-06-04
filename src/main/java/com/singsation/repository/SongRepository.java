package com.singsation.repository;

import com.singsation.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    
    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Song> searchSongs(@Param("query") String query);
    
    // Get only active songs (visible to users)
    List<Song> findByIsActiveTrue();
    
    // Get active songs with pagination
    Page<Song> findByIsActiveTrue(Pageable pageable);
    
    // Search only active songs
    @Query("SELECT s FROM Song s WHERE s.isActive = true AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Song> searchActiveSongs(@Param("query") String query);
}