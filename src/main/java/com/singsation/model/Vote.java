package com.singsation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voter_user_id", nullable = false)
    private Long voterUserId;

    @Column(name = "artist_userid", nullable = false)
    private String artistUserid;

    @Column(name = "entry_id")
    private Long entryId;

    @Column(nullable = false)
    private int cost;

    @Column(name = "vote_type", nullable = false)
    private String voteType = "COIN"; // "COIN", "ROSE", "UNIVERSE"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Vote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVoterUserId() { return voterUserId; }
    public void setVoterUserId(Long voterUserId) { this.voterUserId = voterUserId; }
    public String getArtistUserid() { return artistUserid; }
    public void setArtistUserid(String artistUserid) { this.artistUserid = artistUserid; }
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public String getVoteType() { return voteType; }
    public void setVoteType(String voteType) { this.voteType = voteType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
