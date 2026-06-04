package com.singsation.service;

import com.singsation.model.Admin;
import com.singsation.model.WinnerAnnouncement;
import com.singsation.repository.AdminRepository;
import com.singsation.repository.WinnerAnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminWinnerService {

    @Autowired
    private WinnerAnnouncementRepository winnerRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    @Transactional
    public WinnerAnnouncement announceWinner(WinnerAnnouncement announcement) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin currentAdmin = adminRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        announcement.setAnnouncedBy(currentAdmin);
        announcement.setAnnouncedAt(LocalDateTime.now());
        
        Optional<WinnerAnnouncement> existing = winnerRepository.findByCategoryAndIsActiveTrue(announcement.getCategory());
        existing.ifPresent(winner -> {
            winner.setActive(false);
            winnerRepository.save(winner);
        });
        
        announcement.setActive(true);
        return winnerRepository.save(announcement);
    }

    public List<WinnerAnnouncement> getActiveWinners() {
        return winnerRepository.findByIsActiveTrueOrderByAnnouncedAtDesc();
    }

    public Optional<WinnerAnnouncement> getWinnerByCategory(@NonNull String category) {
        return winnerRepository.findByCategoryAndIsActiveTrue(category);
    }

    // ✅ UPDATE WINNER METHOD
    @Transactional
    public WinnerAnnouncement updateWinner(Long id, WinnerAnnouncement updatedWinner) {
        WinnerAnnouncement existing = winnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Winner not found with id: " + id));
        
        existing.setCategory(updatedWinner.getCategory());
        existing.setWinnerName(updatedWinner.getWinnerName());
        existing.setWinnerUserid(updatedWinner.getWinnerUserid());
        existing.setWinnerAge(updatedWinner.getWinnerAge());
        existing.setProvince(updatedWinner.getProvince());
        existing.setMessage(updatedWinner.getMessage());
        
        return winnerRepository.save(existing);
    }

    // ✅ DELETE WINNER METHOD
    @Transactional
    public void deleteWinner(Long id) {
        if (!winnerRepository.existsById(id)) {
            throw new RuntimeException("Winner not found with id: " + id);
        }
        winnerRepository.deleteById(id);
    }
}