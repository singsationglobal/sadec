package com.singsation.repository;

import com.singsation.model.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {
    List<WebhookLog> findByProcessedFalse();
    Optional<WebhookLog> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
}