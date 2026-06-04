package com.singsation.service;

import com.singsation.model.Song;
import com.singsation.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminSongService {

    @Autowired
    private SongRepository songRepository;

    public Page<Song> getAllSongs(Pageable pageable) {
        return songRepository.findAll(pageable);
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
    }

    // ADD THIS METHOD - CREATE SONG
    public Song createSong(Song song) {
        return songRepository.save(song);
    }

    public Song updateSong(Song song) {
        return songRepository.save(song);
    }
    
    public boolean toggleSongVisibility(Long id) {
        Song song = songRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
        
        song.setActive(!song.isActive());
        songRepository.save(song);
        
        return song.isActive();
    }

    public void hideSong(Long id) {
        Song song = songRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
        song.setActive(false);
        songRepository.save(song);
    }

    public void unhideSong(Long id) {
        Song song = songRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
        song.setActive(true);
        songRepository.save(song);
    }

    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }

    public long getTotalSongsCount() {
        return songRepository.count();
    }
}