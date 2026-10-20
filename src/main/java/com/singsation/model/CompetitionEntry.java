package com.singsation.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "competition_entries")
public class CompetitionEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String category;
    
    @Column(name = "artist_name", nullable = false)
    private String artistName;
    
    @Column(nullable = false)
    private String userid; // The unique stage name/ID
    
    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;
    
    private Integer age;
    
    @Column(name = "contact_info")
    private String contactInfo;
    
    @Column(nullable = false)
    private String province;
    
    @Column(name = "entry_date")
    private LocalDateTime entryDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private String status;

    // ✅ ADDED: The missing 'votes' field that is causing the crash
    @Column(nullable = false)
    private Integer votes = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;
    
    public CompetitionEntry() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    
    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    
    public LocalDateTime getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDateTime entryDate) { this.entryDate = entryDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // ✅ ADDED: Getter and Setter for votes
    public Integer getVotes() { return votes; }
    public void setVotes(Integer votes) { this.votes = votes; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
