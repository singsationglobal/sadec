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
    
    // ✅ FIXED: Removed the "Message from Admin" filter so all complaints show in the list
    @Query("SELECT c FROM Complaint c WHERE c.isDeleted = false")
    Page<Complaint> findAllByIsDeletedFalse(Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.isDeleted = false AND c.status = :status")
    Page<Complaint> findAllByIsDeletedFalseAndStatus(@Param("status") String status, Pageable pageable);
    
    Page<Complaint> findByIsDeletedTrue(Pageable pageable);
    long countByIsDeletedFalse();
}
