package com.singsation.service;

import com.singsation.model.AdminLog;
import com.singsation.repository.AdminLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminLogService {

    @Autowired
    private AdminLogRepository adminLogRepository;

    public Page<AdminLog> getLogsByAdmin(Long adminId, Pageable pageable) {
        return adminLogRepository.findByAdminIdOrderByCreatedAtDesc(adminId, pageable);
    }
}