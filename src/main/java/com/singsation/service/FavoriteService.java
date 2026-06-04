package com.singsation.service;

import com.singsation.service.UserActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.singsation.model.Favorite;
import com.singsation.model.Song;
import com.singsation.model.User;
import com.singsation.repository.FavoriteRepository;
import com.singsation.repository.SongRepository;
import com.singsation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    
    @Autowired
    private UserActivityLogService userActivityLogService;

    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Transactional
public void addFavorite(@NonNull Long userId, @NonNull Long songId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    Song song = songRepository.findById(songId)
        .orElseThrow(() -> new RuntimeException("Song not found"));
    
    if (!favoriteRepository.existsByUserAndSong(user, song)) {
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setSong(song);
        favoriteRepository.save(favorite);
        
        // ✅ Log favorite addition
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        userActivityLogService.logActivity(user, "ADD_FAVORITE", "Added song: " + song.getTitle() + " (ID: " + songId + ") to favorites", request);
    }
}
    
    @Transactional
public void removeFavorite(@NonNull Long userId, @NonNull Long songId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    Song song = songRepository.findById(songId)
        .orElseThrow(() -> new RuntimeException("Song not found"));
    
    favoriteRepository.deleteByUserAndSong(user, song);
    
    // ✅ Log favorite removal
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    userActivityLogService.logActivity(user, "REMOVE_FAVORITE", "Removed song: " + song.getTitle() + " (ID: " + songId + ") from favorites", request);
}
    
    @Transactional(readOnly = true)
    public List<Song> getUserFavorites(@NonNull Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return favoriteRepository.findByUser(user)
            .stream()
            .map(Favorite::getSong)
            .collect(Collectors.toList());
    }
}