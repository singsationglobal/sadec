package com.singsation.service;

import com.singsation.model.User;
import com.singsation.model.Vote;
import com.singsation.repository.CoinTransactionRepository;
import com.singsation.repository.UserRepository;
import com.singsation.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminVotingService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoinTransactionRepository coinTransactionRepository;

    /**
     * Total coins received per artist.
     * The value is now SUM(vote.cost) — coins — not the count of vote rows.
     * The map key is still the artist's userid, exactly as before, so the
     * admin dashboard chart keeps working without change.
     */
    public Map<String, Long> getTotalVotesByArtist() {
        List<Object[]> results = voteRepository.countVotesGroupedByArtist();
        Map<String, Long> voteCounts = new HashMap<>();
        for (Object[] row : results) {
            String artistUserid = (String) row[0];
            Number sum = (Number) row[1];
            voteCounts.put(artistUserid, sum == null ? 0L : sum.longValue());
        }
        return voteCounts;
    }

    public List<Map<String, Object>> getVotersForArtist(String artistUserid) {
        List<Vote> votes = voteRepository.findByArtistUserid(artistUserid);
        List<Map<String, Object>> voterDetails = new ArrayList<>();

        for (Vote vote : votes) {
            Optional<User> voterOpt = userRepository.findById(vote.getVoterUserId());
            if (voterOpt.isPresent()) {
                User voter = voterOpt.get();
                Map<String, Object> details = new HashMap<>();
                details.put("voteId", vote.getId());
                details.put("voterUserId", voter.getId());
                details.put("voterName", voter.getName() + " " + voter.getSurname());
                details.put("voterEmail", voter.getEmail());
                details.put("coinsSpentOnVote", vote.getCost());
                details.put("giftType", vote.getGiftType());
                details.put("votedAt", vote.getCreatedAt());
                voterDetails.add(details);
            }
        }
        return voterDetails;
    }

    public Map<String, Object> getUserCoinStats(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Long totalSpent = coinTransactionRepository.sumSpentCoinsByUserId(userId);
        if (totalSpent == null) totalSpent = 0L;

        Long totalBought = coinTransactionRepository.sumPurchasedCoinsByUserId(userId);
        if (totalBought == null) totalBought = 0L;

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("currentBalance", user.getCoinBalance());
        stats.put("totalCoinsBought", totalBought);
        stats.put("totalCoinsSpent", totalSpent);

        return stats;
    }

    public Map<String, Object> getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalVotes = voteRepository.count();

        Long totalCoinsSpentPlatform = coinTransactionRepository.sumAllSpentCoins();
        if (totalCoinsSpentPlatform == null) totalCoinsSpentPlatform = 0L;

        Long totalCoinsPurchasedPlatform = coinTransactionRepository.sumAllPurchasedCoins();
        if (totalCoinsPurchasedPlatform == null) totalCoinsPurchasedPlatform = 0L;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalVotesCast", totalVotes);
        stats.put("totalCoinsSpentPlatform", totalCoinsSpentPlatform);
        stats.put("totalCoinsPurchasedPlatform", totalCoinsPurchasedPlatform);

        return stats;
    }
}
