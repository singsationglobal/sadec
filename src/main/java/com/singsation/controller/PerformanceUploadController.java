package com.singsation.controller;

import com.singsation.model.CompetitionEntry;
import com.singsation.model.User;
import com.singsation.repository.CompetitionEntryRepository;
import com.singsation.repository.UserRepository;
import com.singsation.service.GoogleCloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private UserRepository userRepository;

    /**
     * Upload the user's paid performance video.
     *
     * - The user is identified from the JWT (never trust a userId in the body).
     * - The server finds the user's most recent un-uploaded entry automatically.
     * - One-shot policy: once uploaded, no re-upload on the same entry.
     *
     * On success:
     *   - videoUrl saved
     *   - videoUploadStatus = "COMPLETED"
     *   - votable = true  ← the entry now appears in the VoteScreen
     */
    @PostMapping("/{entryId}/upload-performance")
    public ResponseEntity<?> uploadPerformanceVideo(
            @PathVariable Long entryId,
            @RequestParam("video") MultipartFile videoFile) {
        try {
            // 1. Identify the authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }
            String email = ((UserDetails) auth.getPrincipal()).getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Find the entry and verify ownership
            CompetitionEntry entry = competitionEntryRepository.findById(entryId)
                    .orElseThrow(() -> new RuntimeException("Competition entry not found"));

            if (!entry.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "You do not own this entry"));
            }

            // 3. One-shot policy: refuse re-upload if already done
            if (entry.getVideoUrl() != null && !entry.getVideoUrl().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "This entry already has a performance video. Submit a new competition entry to try again."
                ));
            }

            if (videoFile == null || videoFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No video file provided"));
            }

            // 4. Upload to Google Cloud
            String videoUrl = googleCloudStorageService.uploadFile(videoFile, "performances");

            // 5. Update the entry
            entry.setVideoUrl(videoUrl);
            entry.setVideoUploadStatus("COMPLETED");
            entry.setVotable(true);
            competitionEntryRepository.save(entry);

            return ResponseEntity.ok(Map.of(
                    "message", "Performance uploaded successfully! Your video is now live for voting.",
                    "videoUrl", videoUrl,
                    "entryId", entry.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}
