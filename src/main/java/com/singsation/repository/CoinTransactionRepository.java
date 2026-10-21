package com.singsation.repository;

import com.singsation.model.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
    
    List<CoinTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Calculate total purchased coins
    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.userId = :userId AND c.type = 'PURCHASE'")
    Long sumPurchasedCoinsByUserId(@Param("userId") Long userId);

    // Calculate total spent coins (voting)
    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.userId = :userId AND c.type = 'VOTE_SPEND'")
    Long sumSpentCoinsByUserId(@Param("userId") Long userId);
}
