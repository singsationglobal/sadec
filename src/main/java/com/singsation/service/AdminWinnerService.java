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

    public List<WinnerAnnouncement> getActiveWinners() {
        return winnerRepository.findByIsActiveTrueAndIsDeletedFalseOrderByAnnouncedAtDesc();
    }

    public List<WinnerAnnouncement> getDeletedWinners() {
        return winnerRepository.findByIsDeletedTrueOrderByDeletedAtDesc();
    }

    @Transactional
    public WinnerAnnouncement announceWinner(WinnerAnnouncement announcement) {
        announcement.setActive(true);
        announcement.setDeleted(false);
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

    @Transactional
    public void moveToBin(Long id) {
        WinnerAnnouncement winner = winnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Winner not found"));
        winner.setDeleted(true);
        winner.setDeletedAt(LocalDateTime.now());
        winnerRepository.save(winner);
    }

    @Transactional
    public void restoreFromBin(Long id) {
        WinnerAnnouncement winner = winnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Winner not found"));
        winner.setDeleted(false);
        winner.setDeletedAt(null);
        winnerRepository.save(winner);
    }

    @Transactional
    public void permanentDelete(Long id) {
        winnerRepository.deleteById(id);
    }
}
