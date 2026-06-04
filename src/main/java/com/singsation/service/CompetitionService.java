package com.singsation.service;

import com.singsation.model.CompetitionEntry;
import com.singsation.model.User;
import com.singsation.repository.CompetitionRepository;
import com.singsation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserActivityLogService userActivityLogService;

    @Transactional
    public CompetitionEntry submitEntry(Long userId, CompetitionEntry entry) {
        entry.setUserId(userId);
        CompetitionEntry savedEntry = competitionRepository.save(entry);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setHasCompletedEntry(true);
        userRepository.save(user);
        
        // ✅ Log competition entry activity
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        userActivityLogService.logActivity(user, "COMPETITION_ENTRY", 
            "Submitted competition entry for category: " + entry.getCategory() + 
            " with UserID: " + entry.getUserid() + 
            " from province: " + entry.getProvince(), request);
        
        return savedEntry;
    }
}