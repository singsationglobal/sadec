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
    
    // ✅ UPDATED: Only search non-deleted songs
    @Query("SELECT s FROM Song s WHERE s.isDeleted = false AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Song> searchSongs(@Param("query") String query);
    
    @Query("SELECT s FROM Song s WHERE s.isActive = true AND s.isDeleted = false")
    List<Song> findByIsActiveTrue();
    
    @Query("SELECT s FROM Song s WHERE s.isActive = true AND s.isDeleted = false")
    Page<Song> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT s FROM Song s WHERE s.isActive = true AND s.isDeleted = false AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Song> searchActiveSongs(@Param("query") String query);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.isActive = false WHERE s.id IN :ids")
    void hideSongsBulk(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.isActive = true WHERE s.id IN :ids")
    void unhideSongsBulk(@Param("ids") List<Long> ids);

    // ✅ ADDED: Soft delete, restore, and permanent delete support
    @Query("SELECT s FROM Song s WHERE s.isDeleted = false")
    Page<Song> findAllActiveSongs(Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.isDeleted = true")
    Page<Song> findAllDeletedSongs(Pageable pageable);

    long countByIsDeletedFalse();
}
