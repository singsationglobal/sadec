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

    @Column(name = "winner_name", nullable = false)
    private String winnerName;

    @Column(name = "winner_userid", nullable = false)
    private String winnerUserid;

    @Column(name = "winner_age")
    private Integer winnerAge;

    @Column(nullable = true)
    private String province;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "announced_at")
    private LocalDateTime announcedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announced_by_admin_id", nullable = false)
    @JsonIgnore
    private Admin announcedBy;

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

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public LocalDateTime getAnnouncedAt() { return announcedAt; }
    public void setAnnouncedAt(LocalDateTime announcedAt) { this.announcedAt = announcedAt; }

    public Admin getAnnouncedBy() { return announcedBy; }
    public void setAnnouncedBy(Admin announcedBy) { this.announcedBy = announcedBy; }
}
