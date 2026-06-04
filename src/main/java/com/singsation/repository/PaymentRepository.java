package com.singsation.repository;

import com.singsation.model.Payment;
import com.singsation.model.User;
import com.singsation.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(User user);
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByUserAndSongAndStatus(User user, Song song, String status);
    boolean existsByUserAndSongAndStatusAndDownloadAccessExpiryAfter(
        User user, Song song, String status, LocalDateTime now);
    List<Payment> findByUserId(Long userId);
}