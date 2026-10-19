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
    
    // ✅ UPDATED: Only return complaints the user has NOT archived
    @Query("SELECT c FROM Complaint c WHERE c.user.id = :userId AND c.userArchived = false ORDER BY c.createdAt DESC")
    List<Complaint> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    // ✅ ADDED: Query for user-archived complaints
    @Query("SELECT c FROM Complaint c WHERE c.user.id = :userId AND c.userArchived = true ORDER BY c.updatedAt DESC")
    List<Complaint> findArchivedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Complaint c WHERE c.isDeleted = false")
    Page<Complaint> findAllByIsDeletedFalse(Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.isDeleted = false AND c.status = :status")
    Page<Complaint> findAllByIsDeletedFalseAndStatus(@Param("status") String status, Pageable pageable);
    
    Page<Complaint> findByIsDeletedTrue(Pageable pageable);
    long countByIsDeletedFalse();
}
