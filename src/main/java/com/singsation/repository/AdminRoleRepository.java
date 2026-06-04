package com.singsation.repository;

import com.singsation.model.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {
    Optional<AdminRole> findByName(String name);
}