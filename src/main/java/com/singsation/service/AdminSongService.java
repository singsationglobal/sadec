package com.singsation.service;

import com.singsation.model.Song;
import com.singsation.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminSongService {

    @Autowired
    private SongRepository songRepository;

    public Page<Song> getAllSongs(Pageable pageable) {
        return songRepository.findAllActiveSongs(pageable);
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
    }

    public Song createSong(Song song) {
        song.setDeleted(false);
        return songRepository.save(song);
    }

    public Song updateSong(Song song) {
        return songRepository.save(song);
    }
    
    public void hideSong(Long id) {
        Song song = getSongById(id);
        song.setActive(false);
        songRepository.save(song);
    }

    public void unhideSong(Long id) {
        Song song = getSongById(id);
        song.setActive(true);
        songRepository.save(song);
    }

    @Modifying
    @Transactional
    public void hideSongsBulk(List<Long> ids) {
        songRepository.hideSongsBulk(ids);
    }

    @Modifying
    @Transactional
    public void unhideSongsBulk(List<Long> ids) {
        songRepository.unhideSongsBulk(ids);
    }

    @Transactional
    public void moveToBin(Long id) {
        Song song = getSongById(id);
        song.setDeleted(true);
        song.setDeletedAt(LocalDateTime.now());
        songRepository.save(song);
    }

    @Transactional
    public void restoreFromBin(Long id) {
        Song song = getSongById(id);
        song.setDeleted(false);
        song.setDeletedAt(null);
        songRepository.save(song);
    }

    @Transactional
    public void permanentDelete(Long id) {
        if (!songRepository.existsById(id)) {
            throw new RuntimeException("Song not found");
        }
        songRepository.deleteById(id);
    }

    public Page<Song> getDeletedSongs(Pageable pageable) {
        return songRepository.findAllDeletedSongs(pageable);
    }

    public long getTotalSongsCount() {
        return songRepository.countByIsDeletedFalse();
    }
}
