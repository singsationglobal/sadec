package com.singsation.dto;

import com.singsation.model.User;
import java.util.Map;
import java.util.HashMap;

public class LoginResponse {
    private String token;
    private String userId;
    private Map<String, Object> user;
    private boolean hasCompletedEntry;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.userId = String.valueOf(user.getId());
        this.user = new HashMap<>();
        this.user.put("name", user.getName());
        this.user.put("email", user.getEmail());
        this.user.put("userid", user.getUserid() != null ? user.getUserid() : "SING_" + user.getId());
        this.user.put("contact", user.getContact() != null ? user.getContact() : "");
        this.user.put("winner", user.getWinner());
        this.hasCompletedEntry = user.isHasCompletedEntry();
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Map<String, Object> getUser() {
        return user;
    }
    
    public void setUser(Map<String, Object> user) {
        this.user = user;
    }
    
    public boolean isHasCompletedEntry() {
        return hasCompletedEntry;
    }
    
    public void setHasCompletedEntry(boolean hasCompletedEntry) {
        this.hasCompletedEntry = hasCompletedEntry;
    }
}