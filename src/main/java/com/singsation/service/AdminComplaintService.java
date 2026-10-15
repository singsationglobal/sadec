package com.singsation.service;

import com.singsation.model.Complaint;
import com.singsation.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class AdminComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public Page<Complaint> getComplaints(Pageable pageable, String status) {
        if (status != null && !status.isEmpty()) {
            return complaintRepository.findUserComplaintsByStatus(status, pageable);
        }
        return complaintRepository.findUserComplaints(pageable);
    }

    public Page<Complaint> getDeletedComplaints(Pageable pageable) {
        return complaintRepository.findByIsDeletedTrue(pageable);
    }

    @Transactional
    public Complaint replyToComplaint(Long id, String adminReply, String status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setAdminReply(adminReply);
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
}
