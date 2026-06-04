package com.singsation.service;

import com.singsation.model.Admin;
import com.singsation.model.AdminRole;
import com.singsation.repository.AdminRepository;
import com.singsation.repository.AdminRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AdminStaffService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminRoleRepository adminRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminLogService adminLogService;

    public List<Admin> getAllStaff() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getStaffById(@NonNull Long id) {
        return adminRepository.findById(id);
    }

    @Transactional
    public Admin createStaff(Admin admin, String roleName, String rawPassword) {
        AdminRole role = adminRoleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        admin.setRole(role);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        return adminRepository.save(admin);
    }

    @Transactional
    public void deleteStaff(@NonNull Long id, @NonNull Long currentAdminId) {
        Admin staffToDelete = adminRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Staff not found"));
        Admin currentAdmin = adminRepository.findById(currentAdminId)
            .orElseThrow(() -> new RuntimeException("Current admin not found"));
        
        String staffRole = staffToDelete.getRole().getName();
        String currentRole = currentAdmin.getRole().getName();
        
        // SUPER_ADMIN deletion logic
        if (staffRole.equals("SUPER_ADMIN")) {
            if (!currentRole.equals("SUPER_ADMIN")) {
                throw new SecurityException("Only SUPER_ADMIN can delete another SUPER_ADMIN");
            }
            if (currentAdmin.getId().equals(staffToDelete.getId())) {
                throw new SecurityException("Cannot delete your own SUPER_ADMIN account");
            }
        }
        // ADMIN deletion logic
        else if (staffRole.equals("ADMIN")) {
            if (!currentRole.equals("SUPER_ADMIN") && !currentRole.equals("ADMIN")) {
                throw new SecurityException("Only SUPER_ADMIN or ADMIN can delete ADMIN accounts");
            }
            if (currentRole.equals("ADMIN") && !currentAdmin.getId().equals(staffToDelete.getId())) {
                throw new SecurityException("ADMIN cannot delete another ADMIN account");
            }
        }
        // MODERATOR deletion logic
        else if (staffRole.equals("MODERATOR")) {
            if (!currentRole.equals("SUPER_ADMIN") && !currentRole.equals("ADMIN")) {
                throw new SecurityException("Only SUPER_ADMIN or ADMIN can delete MODERATOR accounts");
            }
        }
        // SUPPORT deletion logic
        else if (staffRole.equals("SUPPORT")) {
            if (!currentRole.equals("SUPER_ADMIN") && !currentRole.equals("ADMIN") && !currentRole.equals("MODERATOR")) {
                throw new SecurityException("Insufficient permissions to delete SUPPORT accounts");
            }
        }
        
        // TODO: Fix this logging call once AdminLogService is shared
        // For now, just print to console
        System.out.println("STAFF DELETED: " + staffToDelete.getEmail() + " (Role: " + staffRole + ") by: " + currentAdmin.getEmail());
        
        adminRepository.deleteById(id);
    }
}