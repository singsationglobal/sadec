package com.singsation.controller;

import com.singsation.model.Song;
import com.singsation.repository.SongRepository;
import com.singsation.service.OracleStorageService;
import com.singsation.service.GoogleCloudStorageService;
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
    private GoogleCloudStorageService googleCloudStorageService;

    @Autowired
    private SongRepository songRepository;

    @PostMapping("/{id}/upload-files")
    public ResponseEntity<?> uploadSongFiles(
            @PathVariable Long id,
            @RequestParam(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "cloudProvider", defaultValue = "oracle") String cloudProvider) {
        
        try {
            Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
            
            Map<String, String> uploadedFiles = new HashMap<>();
            
            if (audioFile != null && !audioFile.isEmpty()) {
                String audioUrl;
                if ("google".equalsIgnoreCase(cloudProvider)) {
                    audioUrl = googleCloudStorageService.uploadFile(audioFile, "songkaraokesadec/audio");
                } else {
                    audioUrl = oracleStorageService.uploadSongAudio(audioFile);
                }
                song.setUrl(audioUrl);
                uploadedFiles.put("audioUrl", audioUrl);
            }
            
            if (videoFile != null && !videoFile.isEmpty()) {
                String videoUrl;
                if ("google".equalsIgnoreCase(cloudProvider)) {
                    videoUrl = googleCloudStorageService.uploadFile(videoFile, "videokaraokesadec/video");
                } else {
                    videoUrl = oracleStorageService.uploadSongVideo(videoFile);
                }
                song.setVideo(videoUrl);
                uploadedFiles.put("videoUrl", videoUrl);
            }

            if (coverImage != null && !coverImage.isEmpty()) {
                String coverUrl;
                if ("google".equalsIgnoreCase(cloudProvider)) {
                    coverUrl = googleCloudStorageService.uploadFile(coverImage, "coverssadec");
                } else {
                    coverUrl = oracleStorageService.uploadFile(coverImage, "covers", OracleStorageService.BucketType.IMAGES);
                }
                song.setCoverImageUrl(coverUrl);
                uploadedFiles.put("coverImageUrl", coverUrl);
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
