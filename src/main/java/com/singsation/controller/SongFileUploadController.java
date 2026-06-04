package com.singsation.controller;

import com.singsation.model.Song;
import com.singsation.repository.SongRepository;
import com.singsation.service.OracleStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/songs")
public class SongFileUploadController {

    @Autowired
    private OracleStorageService oracleStorageService;

    @Autowired
    private SongRepository songRepository;

    /**
     * Upload audio and video files for a song to Oracle buckets
     * POST /api/admin/songs/{id}/upload-files
     */
    @PostMapping("/{id}/upload-files")
    public ResponseEntity<?> uploadSongFiles(
            @PathVariable Long id,
            @RequestParam(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile) {
        
        try {
            Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
            
            Map<String, String> uploadedFiles = new HashMap<>();
            
            if (audioFile != null && !audioFile.isEmpty()) {
                String audioUrl = oracleStorageService.uploadSongAudio(audioFile);
                song.setUrl(audioUrl);
                uploadedFiles.put("audioUrl", audioUrl);
            }
            
            if (videoFile != null && !videoFile.isEmpty()) {
                String videoUrl = oracleStorageService.uploadSongVideo(videoFile);
                song.setVideo(videoUrl);
                uploadedFiles.put("videoUrl", videoUrl);
            }
            
            songRepository.save(song);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Files uploaded successfully");
            response.put("songId", id);
            response.putAll(uploadedFiles);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Upload failed: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}