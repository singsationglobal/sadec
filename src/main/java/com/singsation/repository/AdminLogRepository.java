package com.singsation.repository;

import com.singsation.model.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
    Page<AdminLog> findByAdminIdOrderByCreatedAtDesc(Long adminId, Pageable pageable);
}