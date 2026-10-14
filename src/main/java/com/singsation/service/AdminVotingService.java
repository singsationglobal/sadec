package com.singsation.service;

import com.singsation.model.User;
import com.singsation.model.Vote;
import com.singsation.repository.UserRepository;
import com.singsation.repository.VoteRepository;
import com.singsation.repository.WebhookLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminVotingService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebhookLogRepository webhookLogRepository;

    // 1. Get total votes grouped by Artist User ID
    public Map<String, Long> getTotalVotesByArtist() {
        List<Object[]> results = voteRepository.countVotesGroupedByArtist();
        Map<String, Long> voteCounts = new HashMap<>();
        for (Object[] row : results) {
            voteCounts.put((String) row[0], (Long) row[1]);
        }
        return voteCounts;
    }

    // 2. Get detailed list of who voted for a specific artist
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
                details.put("votedAt", vote.getCreatedAt());
                voterDetails.add(details);
            }
        }
        return voterDetails;
    }

    // 3. Get coin statistics for a specific user
    public Map<String, Object> getUserCoinStats(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Total coins spent on voting
        Long totalSpent = voteRepository.sumCostByVoterUserId(userId);
        if (totalSpent == null) totalSpent = 0L;

        // Total coins bought (calculated from successful webhook logs)
        // Note: You may need to ensure your webhook logs save the 'coinAmount' in a searchable way, 
        // or you can track this in a separate 'CoinTransaction' table. 
        // For now, we estimate based on current balance + spent.
        long totalBought = user.getCoinBalance() + totalSpent;

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("currentBalance", user.getCoinBalance());
        stats.put("totalCoinsBought", totalBought);
        stats.put("totalCoinsSpent", totalSpent);
        
        return stats;
    }

    // 4. Get overall platform coin and voting stats
    public Map<String, Object> getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalVotes = voteRepository.count();
        Long totalCoinsSpentPlatform = voteRepository.sumAllCosts();
        if (totalCoinsSpentPlatform == null) totalCoinsSpentPlatform = 0L;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalVotesCast", totalVotes);
        stats.put("totalCoinsSpentPlatform", totalCoinsSpentPlatform);
        
        return stats;
    }
}
