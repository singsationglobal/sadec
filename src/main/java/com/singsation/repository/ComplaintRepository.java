package com.singsation.repository;

import com.singsation.model.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    // Get complaints by user ID (for user's own complaints)
    List<Complaint> findByUserId(Long userId);
    
    // ADD THIS METHOD - orders by createdAt descending (newest first)
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Get complaints by status (for admin panel filtering)
    Page<Complaint> findByStatus(String status, Pageable pageable);
    
    // Get complaints by user ID with pagination
    Page<Complaint> findByUserId(Long userId, Pageable pageable);
}