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

    // ✅ FIXED: Changed c.userId to c.user.id to match the JPA model
    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.user.id = :userId AND c.type = 'PURCHASE'")
    Long sumPurchasedCoinsByUserId(@Param("userId") Long userId);

    // ✅ FIXED: Changed c.userId to c.user.id to match the JPA model
    @Query("SELECT SUM(c.amount) FROM CoinTransaction c WHERE c.user.id = :userId AND c.type = 'VOTE_SPEND'")
    Long sumSpentCoinsByUserId(@Param("userId") Long userId);
}
