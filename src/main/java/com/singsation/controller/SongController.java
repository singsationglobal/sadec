package com.singsation.controller;

import com.singsation.dto.SongDTO;
import com.singsation.model.Song;
import com.singsation.service.PaymentService;
import com.singsation.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private PaymentService paymentService;
    
    // ONLY return active songs to karaoke app
    @GetMapping
    public List<SongDTO> getAllSongs() {
        return songService.getVisibleSongs()
            .stream()
            .map(SongDTO::new)
            .collect(Collectors.toList());
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchSongs(@RequestParam(required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search query is required");
            return ResponseEntity.badRequest().body(error);
        }
        
        try {
            List<Song> songs = songService.searchSongs(query.trim());
            List<SongDTO> activeSongs = songs.stream()
                .filter(Song::isActive)
                .map(SongDTO::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(activeSongs);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Search failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getSong(@PathVariable @NonNull Long id) {
        try {
            Song song = songService.getSongById(id);
            // Only return if active
            if (!song.isActive()) {
                return ResponseEntity.status(404).body(Map.of("error", "Song not available"));
            }
            return ResponseEntity.ok(new SongDTO(song));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Song not found");
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve song");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/{songId}/download")
    public ResponseEntity<?> downloadVideo(
            @PathVariable @NonNull Long songId,
            @RequestParam @NonNull Long userId) {
        
        try {
            boolean hasAccess = paymentService.hasDownloadAccess(userId, songId);
            
            if (!hasAccess) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Payment required to download this video");
                errorResponse.put("paymentRequired", true);
                errorResponse.put("amount", 50.00);
                errorResponse.put("currency", "ZAR");
                return ResponseEntity.status(403).body(errorResponse);
            }
            
            Song song = songService.getSongById(songId);
            
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("downloadUrl", song.getVideo());
            successResponse.put("title", song.getTitle());
            successResponse.put("artist", song.getArtist());
            successResponse.put("message", "Download access granted");
            
            return ResponseEntity.ok(successResponse);
            
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}