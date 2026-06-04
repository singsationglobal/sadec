package com.singsation.service;

import com.singsation.model.Complaint;
import com.singsation.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AdminComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public Page<Complaint> getAllComplaints(@NonNull Pageable pageable, String status) {
        if (status != null && !status.isEmpty()) {
            return complaintRepository.findByStatus(status, pageable);
        }
        return complaintRepository.findAll(pageable);
    }

    public Complaint getComplaintById(@NonNull Long id) {
        return complaintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }
    
    public Complaint replyToComplaint(@NonNull Long id, String adminReply, String status) {
        Complaint complaint = getComplaintById(id);
        complaint.setAdminReply(adminReply);
        complaint.setStatus(status);
        complaint.setUpdatedAt(java.time.LocalDateTime.now());
        return complaintRepository.save(complaint);
    }
    
    public long getTotalComplaintsCount() {
        return complaintRepository.count();
    }
}