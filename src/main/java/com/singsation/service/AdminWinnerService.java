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
        
        // ✅ UPDATED: Only deactivate if the existing winner is NOT deleted
        Optional<WinnerAnnouncement> existing = winnerRepository.findByCategoryAndIsActiveTrueAndIsDeletedFalse(announcement.getCategory());
        existing.ifPresent(winner -> {
            winner.setActive(false);
            winnerRepository.save(winner);
        });
        
        announcement.setActive(true);
        return winnerRepository.save(announcement);
    }

    // ✅ UPDATED: Only fetch winners that are active AND NOT deleted
    public List<WinnerAnnouncement> getActiveWinners() {
        return winnerRepository.findByIsActiveTrueAndIsDeletedFalseOrderByAnnouncedAtDesc();
    }

    public Optional<WinnerAnnouncement> getWinnerByCategory(@NonNull String category) {
        return winnerRepository.findByCategoryAndIsActiveTrueAndIsDeletedFalse(category);
    }

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

    // ✅ NEW: Move to Recycle Bin (Soft Delete)
    @Transactional
    public void moveToRecycleBin(Long id) {
        WinnerAnnouncement winner = winnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Winner not found with id: " + id));
        
        winner.setActive(false);
        winner.setIsDeleted(true);
        winner.setDeletedAt(LocalDateTime.now());
        winnerRepository.save(winner);
    }

    // ✅ NEW: Permanent Delete (Super Admin Only)
    @Transactional
    public void permanentDelete(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin currentAdmin = adminRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!"SUPER_ADMIN".equals(currentAdmin.getRole().getName())) {
            throw new RuntimeException("Access Denied: Only Super Admins can permanently delete.");
        }

        if (!winnerRepository.existsById(id)) {
            throw new RuntimeException("Winner not found with id: " + id);
        }
        winnerRepository.deleteById(id);
    }
}
