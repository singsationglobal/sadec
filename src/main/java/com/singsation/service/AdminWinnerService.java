package com.singsation.service;

import com.singsation.model.WinnerAnnouncement;
import com.singsation.repository.WinnerAnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminWinnerService {

    @Autowired
    private WinnerAnnouncementRepository winnerRepository;

    // ✅ Returns ALL active winners, stacked newest first (1st, 2nd, 3rd)
    public List<WinnerAnnouncement> getActiveWinners() {
        return winnerRepository.findByIsActiveTrueAndIsDeletedFalseOrderByAnnouncedAtDesc();
    }

    // ✅ Returns ONLY deleted winners for the Recycle Bin
    public List<WinnerAnnouncement> getDeletedWinners() {
        return winnerRepository.findByIsDeletedTrueOrderByDeletedAtDesc();
    }

    @Transactional
    public WinnerAnnouncement announceWinner(WinnerAnnouncement announcement) {
        announcement.setIsActive(true);
        announcement.setIsDeleted(false);
        announcement.setAnnouncedAt(LocalDateTime.now());
        return winnerRepository.save(announcement);
    }

    @Transactional
    public WinnerAnnouncement updateWinner(Long id, WinnerAnnouncement updatedData) {
        WinnerAnnouncement existing = winnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Winner not found"));
        
        existing.setCategory(updatedData.getCategory());
        existing.setWinnerName(updatedData.getWinnerName());
        existing.setWinnerUserid(updatedData.getWinnerUserid());
        existing.setWinnerAge(updatedData.getWinnerAge());
        existing.setProvince(updatedData.getProvince());
        existing.setMessage(updatedData.getMessage());
        
        return winnerRepository.save(existing);
    }

    // ✅ Moves to Recycle Bin (No popup needed on frontend)
    @Transactional
    public void moveToBin(Long id) {
        WinnerAnnouncement winner = winnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Winner not found"));
        winner.setIsDeleted(true);
        winner.setDeletedAt(LocalDateTime.now());
        winnerRepository.save(winner);
    }

    @Transactional
    public void restoreFromBin(Long id) {
        WinnerAnnouncement winner = winnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Winner not found"));
        winner.setIsDeleted(false);
        winner.setDeletedAt(null);
        winnerRepository.save(winner);
    }

    @Transactional
    public void permanentDelete(Long id) {
        winnerRepository.deleteById(id);
    }
}
