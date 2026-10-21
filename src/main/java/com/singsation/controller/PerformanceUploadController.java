package com.singsation.controller;

import com.singsation.model.CompetitionEntry;
import com.singsation.repository.CompetitionEntryRepository;
import com.singsation.service.GoogleCloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/competitions")
public class PerformanceUploadController {

    @Autowired
    private GoogleCloudStorageService googleCloudStorageService;

    @Autowired
    private CompetitionEntryRepository competitionEntryRepository;

    @PostMapping("/{entryId}/upload-performance")
    public ResponseEntity<?> uploadPerformanceVideo(
            @PathVariable Long entryId,
            @RequestParam("video") MultipartFile videoFile) {
        try {
            CompetitionEntry entry = competitionEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Competition entry not found"));

            // 1. Upload to Google Cloud
            String videoUrl = googleCloudStorageService.uploadFile(videoFile, "performances");

            // 2. Update the entry
            entry.setVideoUrl(videoUrl);
            entry.setVideoUploadStatus("COMPLETED");
            entry.setVotable(true); // ✅ THIS ACTIVATES VOTING
            competitionEntryRepository.save(entry);

            return ResponseEntity.ok(Map.of(
                "message", "Performance uploaded successfully! Your video is now live for voting.",
                "videoUrl", videoUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}
