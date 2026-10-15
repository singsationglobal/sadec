package com.singsation.repository;

import com.singsation.model.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByUserId(Long userId);
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<Complaint> findByStatusAndIsDeletedFalse(String status, Pageable pageable);
    Page<Complaint> findByIsDeletedFalse(Pageable pageable);
    
    // ✅ ADDED: For the Recycle Bin
    Page<Complaint> findByIsDeletedTrue(Pageable pageable);
    long countByIsDeletedFalse();
}
