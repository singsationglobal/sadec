package com.singsation.repository;

import com.singsation.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    
    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Song> searchSongs(@Param("query") String query);
    
    List<Song> findByIsActiveTrue();
    
    Page<Song> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT s FROM Song s WHERE s.isActive = true AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Song> searchActiveSongs(@Param("query") String query);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.isActive = false WHERE s.id IN :ids")
    void hideSongsBulk(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.isActive = true WHERE s.id IN :ids")
    void unhideSongsBulk(@Param("ids") List<Long> ids);
}
