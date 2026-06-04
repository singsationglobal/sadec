package com.singsation.controller;

import com.singsation.service.OracleStorageService;
import com.singsation.service.UserActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import com.singsation.dto.SongDTO;
import com.singsation.model.Song;
import com.singsation.model.User;
import com.singsation.service.FavoriteService;
import com.singsation.service.SongService;
import com.singsation.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserActivityLogService userActivityLogService;

    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    private UserService userService;
    
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private OracleStorageService oracleStorageService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable @NonNull Long userId) {
        User user = userService.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        HashMap<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("surname", user.getSurname() != null ? user.getSurname() : "");
        response.put("email", user.getEmail());
        response.put("userid", user.getUserid() != null ? user.getUserid() : "");
        response.put("contact", user.getContact() != null ? user.getContact() : "");
        response.put("winner", user.getWinner() != null ? user.getWinner() : "www.singsationsadec.com");
        response.put("signupMethod", user.getSignupMethod());
        response.put("alternativeContact", user.getAlternativeContact() != null ? user.getAlternativeContact() : "");
        response.put("province", user.getProvince() != null ? user.getProvince() : "");
        response.put("age", user.getAge() != null ? user.getAge() : 0);
        response.put("hasCompletedEntry", user.isHasCompletedEntry());
        response.put("profileImageUrl", user.getProfileImageUrl());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable @NonNull Long userId, @RequestBody Map<String, Object> userDetails) {
        try {
            String newUserid = userDetails.containsKey("userid") ? userDetails.get("userid").toString() : null;
            
            if (newUserid != null && !newUserid.isEmpty()) {
                var existingUser = userService.findByUserid(newUserid);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "User ID already taken. Please choose another."
                    ));
                }
            }
            
            User updatedUser = userService.updateUserFromMap(userId, userDetails);
            
            // Log profile update activity
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String changes = "";
            if (userDetails.containsKey("userid")) changes += "UserID changed, ";
            if (userDetails.containsKey("name")) changes += "Name changed, ";
            if (userDetails.containsKey("province")) changes += "Province changed, ";
            if (changes.isEmpty()) changes = "Profile updated";
            userActivityLogService.logActivity(updatedUser, "PROFILE_UPDATE", changes, request);
            
            HashMap<String, Object> response = new HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("name", updatedUser.getName());
            response.put("surname", updatedUser.getSurname() != null ? updatedUser.getSurname() : "");
            response.put("email", updatedUser.getEmail());
            response.put("userid", updatedUser.getUserid() != null ? updatedUser.getUserid() : "");
            response.put("contact", updatedUser.getContact() != null ? updatedUser.getContact() : "");
            response.put("winner", updatedUser.getWinner() != null ? updatedUser.getWinner() : "www.singsationsadec.com");
            response.put("signupMethod", updatedUser.getSignupMethod());
            response.put("alternativeContact", updatedUser.getAlternativeContact() != null ? updatedUser.getAlternativeContact() : "");
            response.put("province", updatedUser.getProvince() != null ? updatedUser.getProvince() : "");
            response.put("age", updatedUser.getAge() != null ? updatedUser.getAge() : 0);
            response.put("hasCompletedEntry", updatedUser.isHasCompletedEntry());
            response.put("profileImageUrl", updatedUser.getProfileImageUrl());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{userId}/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable Long userId,
            @RequestParam("profileImage") MultipartFile imageFile) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Upload to Oracle storage bucket
            String imageUrl = oracleStorageService.uploadStorageFile(imageFile, "profiles");
            
            // Save URL to user profile using dedicated profileImageUrl field
            user.setProfileImageUrl(imageUrl);
            userService.saveUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("profileImageUrl", imageUrl);
            response.put("message", "Profile image uploaded successfully");
            
            // Log activity
            userActivityLogService.logActivity(user, "PROFILE_IMAGE_UPLOAD", "Uploaded profile image", httpRequest);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload profile image: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/favorites/{songId}")
    public ResponseEntity<?> addFavorite(@PathVariable @NonNull Long userId, @PathVariable @NonNull Long songId) {
        favoriteService.addFavorite(userId, songId);
        return ResponseEntity.ok(Map.of("message", "Favorite added"));
    }
    
    @DeleteMapping("/{userId}/favorites/{songId}")
    public ResponseEntity<?> removeFavorite(@PathVariable @NonNull Long userId, @PathVariable @NonNull Long songId) {
        favoriteService.removeFavorite(userId, songId);
        return ResponseEntity.ok(Map.of("message", "Favorite removed"));
    }
    
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<?> getFavorites(@PathVariable @NonNull Long userId) {
        try {
            List<Song> favorites = favoriteService.getUserFavorites(userId);
            List<SongDTO> dtoList = favorites.stream()
                .map(SongDTO::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/songs/{songId}/play")
    public ResponseEntity<?> trackSongPlay(@PathVariable @NonNull Long userId, @PathVariable @NonNull Long songId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Song song = songService.getSongById(songId);
            
            userActivityLogService.logActivity(user, "PLAY_SONG", "Played song: " + song.getTitle() + " (ID: " + songId + ")", httpRequest);
            
            logger.info("User {} played song {} - {} by {}", 
                userId, songId, song.getTitle(), song.getArtist());
            
            return ResponseEntity.ok(Map.of(
                "message", "Play tracked successfully",
                "userId", userId,
                "songId", songId,
                "songTitle", song.getTitle(),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{userId}/account")
    public ResponseEntity<?> deleteAccount(
            @PathVariable @NonNull Long userId,
            @RequestBody Map<String, String> request) {
        
        String password = request.get("password");
        
        try {
            userService.deleteUserAccount(userId, password);
            return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}