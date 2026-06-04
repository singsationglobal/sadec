package com.singsation.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String surname;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true)
    private String contact;
    
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true)
    private String userid;
    
    private String winner = "www.singsationsadec.com";
    
    @Column(name = "has_completed_entry", nullable = false)
    private boolean hasCompletedEntry = false;
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    @Column(name = "signup_method", nullable = false)
    private String signupMethod = "EMAIL";
    
    @Column(name = "alternative_contact")
    private String alternativeContact;
    
    @Column(name = "province")
    private String province;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CompetitionEntry> entries;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    public boolean isHasCompletedEntry() { return hasCompletedEntry; }
    public void setHasCompletedEntry(boolean hasCompletedEntry) { this.hasCompletedEntry = hasCompletedEntry; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getSignupMethod() { return signupMethod; }
    public void setSignupMethod(String signupMethod) { this.signupMethod = signupMethod; }
    public String getAlternativeContact() { return alternativeContact; }
    public void setAlternativeContact(String alternativeContact) { this.alternativeContact = alternativeContact; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public List<Favorite> getFavorites() { return favorites; }
    public void setFavorites(List<Favorite> favorites) { this.favorites = favorites; }
    public List<CompetitionEntry> getEntries() { return entries; }
    public void setEntries(List<CompetitionEntry> entries) { this.entries = entries; }
}