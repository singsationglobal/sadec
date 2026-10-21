package com.singsation.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "coin_transactions")
public class CoinTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String type; // "PURCHASE", "VOTE_SPEND", "ADMIN_ADD"

    @Column(nullable = false)
    private int amount;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Column
    private String reference;

    @Column(length = 500)
    private String description;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "vote_id")
    private Long voteId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public CoinTransaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getVoteId() { return voteId; }
    public void setVoteId(Long voteId) { this.voteId = voteId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
