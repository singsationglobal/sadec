package com.singsation.repository;

import com.singsation.model.UserActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    
    Page<UserActivityLog> findByUserId(Long userId, Pageable pageable);
    
    Page<UserActivityLog> findByAction(String action, Pageable pageable);
    
    @Query("SELECT l FROM UserActivityLog l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    Page<UserActivityLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate, 
                                          Pageable pageable);
    
    @Query("SELECT COUNT(DISTINCT l.user.id) FROM UserActivityLog l WHERE l.action = 'LOGIN' AND l.createdAt BETWEEN :startDate AND :endDate")
    long countDistinctUsersByActionAndDateRange(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(l) FROM UserActivityLog l WHERE l.action = :action AND l.createdAt BETWEEN :startDate AND :endDate")
    long countByActionAndDateRange(@Param("action") String action,
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
}