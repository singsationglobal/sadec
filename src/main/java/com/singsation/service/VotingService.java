package com.singsation.service;

import com.singsation.model.CoinTransaction;
import com.singsation.model.CompetitionEntry;
import com.singsation.model.User;
import com.singsation.model.Vote;
import com.singsation.repository.CoinTransactionRepository;
import com.singsation.repository.CompetitionEntryRepository;
import com.singsation.repository.UserRepository;
import com.singsation.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VotingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CoinTransactionRepository coinTransactionRepository;
    
    @Autowired
    private CompetitionEntryRepository competitionEntryRepository;

    @Transactional
    public void castVote(Long voterUserId, Long entryId, int cost, String voteType) {
        User voter = userRepository.findById(voterUserId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        CompetitionEntry entry = competitionEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Competition entry not found"));

        if (!entry.isVotable()) {
            throw new RuntimeException("This entry is not available for voting yet.");
        }

        // 1. Check if user has enough coins
        if (voter.getCoinBalance() < cost) {
            throw new RuntimeException("Insufficient coin balance");
        }

        // 2. Deduct coins
        int oldBalance = voter.getCoinBalance();
        int newBalance = oldBalance - cost;
        voter.setCoinBalance(newBalance);
        userRepository.save(voter);

        // 3. Record the vote
        Vote vote = new Vote();
        vote.setVoterUserId(voterUserId);
        vote.setArtistUserid(entry.getUserid());
        vote.setEntryId(entryId);
        vote.setCost(cost);
        vote.setVoteType(voteType != null ? voteType : "COIN");
        vote = voteRepository.save(vote);

        // 4. Record in the coin ledger
        CoinTransaction tx = new CoinTransaction();
        tx.setUser(voter);
        tx.setType("VOTE_SPEND");
        tx.setAmount(cost);
        tx.setBalanceAfter(newBalance);
        tx.setDescription("Voted for " + entry.getArtistName() + " with " + cost + " " + voteType);
        tx.setVoteId(vote.getId());
        coinTransactionRepository.save(tx);
    }

    public int getCoinBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCoinBalance();
    }

    @Transactional
    public void addCoins(Long userId, int coinAmount, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        int oldBalance = user.getCoinBalance();
        int newBalance = oldBalance + coinAmount;
        user.setCoinBalance(newBalance);
        userRepository.save(user);

        CoinTransaction tx = new CoinTransaction();
        tx.setUser(user);
        tx.setType("ADMIN_ADD");
        tx.setAmount(coinAmount);
        tx.setBalanceAfter(newBalance);
        tx.setDescription(reason);
        coinTransactionRepository.save(tx);
    }
}
