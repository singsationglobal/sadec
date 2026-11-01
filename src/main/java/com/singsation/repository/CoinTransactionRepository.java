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

    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.user.id = :userId AND c.type = 'PURCHASE'")
    Long sumPurchasedCoinsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.user.id = :userId AND c.type = 'VOTE_SPEND'")
    Long sumSpentCoinsByUserId(@Param("userId") Long userId);

    // ✅ NEW: Platform-wide totals for the admin dashboard
    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.type = 'PURCHASE'")
    Long sumAllPurchasedCoins();

    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.type = 'VOTE_SPEND'")
    Long sumAllSpentCoins();
}
