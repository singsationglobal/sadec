package com.singsation.repository;

import com.singsation.model.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByUserId(Long userId);
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // ✅ UPDATED: Hides "Message from Admin" so you don't reply to yourself
    @Query("SELECT c FROM Complaint c WHERE c.isDeleted = false AND c.subject NOT LIKE 'Message from Admin:%'")
    Page<Complaint> findUserComplaints(Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.isDeleted = false AND c.subject NOT LIKE 'Message from Admin:%' AND c.status = :status")
    Page<Complaint> findUserComplaintsByStatus(@Param("status") String status, Pageable pageable);
    
    Page<Complaint> findByIsDeletedTrue(Pageable pageable);
    long countByIsDeletedFalse();
}
