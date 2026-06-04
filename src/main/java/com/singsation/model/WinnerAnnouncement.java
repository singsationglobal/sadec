package com.singsation.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "winner_announcements")
public class WinnerAnnouncement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String winnerName;

    @Column(nullable = false)
    private String winnerUserid;

    @Column(nullable = false)
    private Integer winnerAge;

    @Column(nullable = false)
    private String province;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime announcedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "announced_by_admin_id", nullable = false)
    @JsonIgnore
    private Admin announcedBy;

    public WinnerAnnouncement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }
    public String getWinnerUserid() { return winnerUserid; }
    public void setWinnerUserid(String winnerUserid) { this.winnerUserid = winnerUserid; }
    public Integer getWinnerAge() { return winnerAge; }
    public void setWinnerAge(Integer winnerAge) { this.winnerAge = winnerAge; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getAnnouncedAt() { return announcedAt; }
    public void setAnnouncedAt(LocalDateTime announcedAt) { this.announcedAt = announcedAt; }
    public Admin getAnnouncedBy() { return announcedBy; }
    public void setAnnouncedBy(Admin announcedBy) { this.announcedBy = announcedBy; }
}