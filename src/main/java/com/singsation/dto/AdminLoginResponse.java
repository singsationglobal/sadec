package com.singsation.dto;

import com.singsation.model.Admin;
import java.util.HashMap;
import java.util.Map;

public class AdminLoginResponse {
    private String token;
    private Map<String, Object> admin;

    public AdminLoginResponse(String token, Admin admin) {
        this.token = token;
        this.admin = new HashMap<>();
        if (admin != null) {
            this.admin.put("id", admin.getId());
            this.admin.put("name", admin.getName());
            this.admin.put("surname", admin.getSurname());
            this.admin.put("email", admin.getEmail());
            this.admin.put("isActive", admin.isActive());
            
            if (admin.getRole() != null) {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("id", admin.getRole().getId());
                roleMap.put("name", admin.getRole().getName());
                roleMap.put("permissions", admin.getRole().getPermissions());
                this.admin.put("role", roleMap);
            }
        }
    }

    public String getToken() { return token; }
    public Map<String, Object> getAdmin() { return admin; }
}