package com.singsation.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String contact;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column
    private String surname;

    @Column
    private String userid;

    @Column
    private String winner;

    @Column(name = "has_completed_entry")
    private boolean hasCompletedEntry = false;

    @Column
    private Integer age;

    @Column(name = "artist_name")
    private String artistName;

    @Column(name = "alternative_contact")
    private String alternativeContact;

    @Column(name = "signup_method")
    private String signupMethod;

    @Column
    private String province;

    @Column(name = "is_active")
    private boolean isActive = true;

    // ✅ ADDED: Soft delete fields
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "coin_balance")
    private int coinBalance = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Complaint> complaints;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Payment> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserActivityLog> activityLogs;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    public boolean isHasCompletedEntry() { return hasCompletedEntry; }
    public void setHasCompletedEntry(boolean hasCompletedEntry) { this.hasCompletedEntry = hasCompletedEntry; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getAlternativeContact() { return alternativeContact; }
    public void setAlternativeContact(String alternativeContact) { this.alternativeContact = alternativeContact; }
    public String getSignupMethod() { return signupMethod; }
    public void setSignupMethod(String signupMethod) { this.signupMethod = signupMethod; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // ✅ ADDED: Getters and Setters for soft delete
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public int getCoinBalance() { return coinBalance; }
    public void setCoinBalance(int coinBalance) { this.coinBalance = coinBalance; }
    public List<Complaint> getComplaints() { return complaints; }
    public void setComplaints(List<Complaint> complaints) { this.complaints = complaints; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public List<Favorite> getFavorites() { return favorites; }
    public void setFavorites(List<Favorite> favorites) { this.favorites = favorites; }
    public List<UserActivityLog> getActivityLogs() { return activityLogs; }
    public void setActivityLogs(List<UserActivityLog> activityLogs) { this.activityLogs = activityLogs; }
}
