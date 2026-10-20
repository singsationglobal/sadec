package com.singsation.service;

import com.singsation.model.Complaint;
import com.singsation.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public Page<Complaint> getComplaints(Pageable pageable, String status) {
        if (status != null && !status.isEmpty()) {
            return complaintRepository.findAllByIsDeletedFalseAndStatus(status, pageable);
        }
        return complaintRepository.findAllByIsDeletedFalse(pageable);
    }

    public Page<Complaint> getDeletedComplaints(Pageable pageable) {
        return complaintRepository.findByIsDeletedTrue(pageable);
    }

    @Transactional
    public Complaint replyToComplaint(Long id, String adminReply, String status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        // ✅ FIX: Append to existing reply instead of overwriting
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        String newReplyBlock = "\n\n==============================\nAdmin Reply (" + timestamp + "):\n" + adminReply;
        String currentReply = complaint.getAdminReply() == null ? "" : complaint.getAdminReply();
        
        complaint.setAdminReply(currentReply + newReplyBlock);
        complaint.setStatus(status);
        complaint.setUpdatedAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }

    @Transactional
    public void moveToBin(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setDeleted(true);
        complaint.setDeletedAt(LocalDateTime.now());
        complaintRepository.save(complaint);
    }

    @Transactional
    public void restoreFromBin(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setDeleted(false);
        complaint.setDeletedAt(null);
        complaintRepository.save(complaint);
    }

    @Transactional
    public void permanentDelete(Long id) {
        if (!complaintRepository.existsById(id)) {
            throw new RuntimeException("Complaint not found");
        }
        complaintRepository.deleteById(id);
    }

    public long getTotalComplaintsCount() {
        return complaintRepository.countByIsDeletedFalse();
    }

    @Transactional
    public void archiveUserComplaint(Long id, Long userId) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        if (complaint.getUser().getId().equals(userId)) {
            complaint.setUserArchived(true);
            complaintRepository.save(complaint);
        }
    }

    @Transactional
    public void restoreUserComplaint(Long id, Long userId) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        if (complaint.getUser().getId().equals(userId)) {
            complaint.setUserArchived(false);
            complaintRepository.save(complaint);
        }
    }
}
