package com.singsation.controller;

import com.singsation.model.Song;
import com.singsation.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/songs")
public class AdminSongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public ResponseEntity<?> getAllSongs(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        var songs = songService.getAllSongs(PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("songs", songs.getContent());
        response.put("totalPages", songs.getTotalPages());
        response.put("totalElements", songs.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createSong(@RequestBody Map<String, String> songData) {
        try {
            Song song = new Song();
            song.setTitle(songData.get("title"));
            song.setArtist(songData.get("artist"));
            song.setUrl(songData.get("url") != null ? songData.get("url") : "");
            song.setVideo(songData.get("video") != null ? songData.get("video") : "");
            song.setActive(true);
            
            Song saved = songRepository.save(song); 
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("title", saved.getTitle());
            response.put("artist", saved.getArtist());
            response.put("message", "Song created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSong(@PathVariable Long id, @RequestBody Map<String, String> songData) {
        try {
            Song existingSong = songService.getSongById(id);
            
            if (songData.containsKey("title") && songData.get("title") != null) {
                existingSong.setTitle(songData.get("title"));
            }
            if (songData.containsKey("artist") && songData.get("artist") != null) {
                existingSong.setArtist(songData.get("artist"));
            }
            if (songData.containsKey("url") && songData.get("url") != null) {
                existingSong.setUrl(songData.get("url"));
            }
            if (songData.containsKey("video") && songData.get("video") != null) {
                existingSong.setVideo(songData.get("video"));
            }
            
            Song updated = songRepository.save(existingSong);
            return ResponseEntity.ok(Map.of(
                "message", "Song updated successfully",
                "song", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/toggle-visibility")
    public ResponseEntity<?> toggleSongVisibility(@PathVariable @NonNull Long id) {
        Song song = songService.getSongById(id);
        song.setActive(!song.isActive());
        songRepository.save(song);
        return ResponseEntity.ok(Map.of(
            "message", "Song is now " + (song.isActive() ? "visible" : "hidden"),
            "isActive", song.isActive()
        ));
    }
    
    @PostMapping("/{id}/hide")
    public ResponseEntity<?> hideSong(@PathVariable @NonNull Long id) {
        songService.hideSongsBulk(List.of(id));
        return ResponseEntity.ok(Map.of("message", "Song hidden from users"));
    }
    
    @PostMapping("/{id}/unhide")
    public ResponseEntity<?> unhideSong(@PathVariable @NonNull Long id) {
        songService.unhideSongsBulk(List.of(id));
        return ResponseEntity.ok(Map.of("message", "Song visible to users"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable @NonNull Long id) {
        songService.deleteSongById(id);
        return ResponseEntity.ok(Map.of("message", "Song deleted"));
    }

    @PostMapping("/hide-bulk")
    public ResponseEntity<?> hideSongsBulk(@RequestBody List<Long> songIds) {
        try {
            if (songIds == null || songIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No song IDs provided"));
            }
            songService.hideSongsBulk(songIds);
            return ResponseEntity.ok(Map.of("message", "Songs hidden successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to hide songs: " + e.getMessage()));
        }
    }

    @PostMapping("/unhide-bulk")
    public ResponseEntity<?> unhideSongsBulk(@RequestBody List<Long> songIds) {
        try {
            if (songIds == null || songIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No song IDs provided"));
            }
            songService.unhideSongsBulk(songIds);
            return ResponseEntity.ok(Map.of("message", "Songs unhidden successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to unhide songs: " + e.getMessage()));
        }
    }
}
