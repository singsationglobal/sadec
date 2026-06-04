package com.singsation.service;

import com.singsation.model.User;
import com.singsation.model.UserActivityLog;
import com.singsation.repository.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class UserActivityLogService {
    
    @Autowired
    private UserActivityLogRepository userActivityLogRepository;
    
    @Transactional
    public void logActivity(User user, String action, String details, HttpServletRequest request) {
        if (user == null) return;
        
        String ipAddress = null;
        String deviceInfo = null;
        
        if (request != null) {
            ipAddress = getClientIp(request);
            deviceInfo = request.getHeader("User-Agent");
            if (deviceInfo != null && deviceInfo.length() > 500) {
                deviceInfo = deviceInfo.substring(0, 497) + "...";
            }
        }
        
        UserActivityLog log = new UserActivityLog(user, action, details, ipAddress, deviceInfo);
        userActivityLogRepository.save(log);
    }
    
    @Transactional
    public void logActivity(User user, String action, String details) {
        logActivity(user, action, details, null);
    }
    
    public Page<UserActivityLog> getUserActivity(Long userId, Pageable pageable) {
        return userActivityLogRepository.findByUserId(userId, pageable);
    }
    
    public Page<UserActivityLog> getAllActivity(Pageable pageable) {
        return userActivityLogRepository.findAll(pageable);
    }
    
    public Page<UserActivityLog> getActivityByAction(String action, Pageable pageable) {
        return userActivityLogRepository.findByAction(action, pageable);
    }
    
    public long getActiveUsersToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        // ✅ FIXED: Order of parameters - repository expects (startDate, endDate)
        return userActivityLogRepository.countDistinctUsersByActionAndDateRange(startOfDay, LocalDateTime.now());
    }
    
    public long getTotalLoginsToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        // ✅ FIXED: Repository method expects (action, startDate, endDate) - but check your repository signature
        return userActivityLogRepository.countByActionAndDateRange("LOGIN", startOfDay, LocalDateTime.now());
    }
    
    public long getTotalSongPlaysToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        // ✅ FIXED: Repository method expects (action, startDate, endDate)
        return userActivityLogRepository.countByActionAndDateRange("PLAY_SONG", startOfDay, LocalDateTime.now());
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}