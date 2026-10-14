package com.singsation.service;

import com.singsation.model.User;
import com.singsation.model.Vote;
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

    @Transactional
    public void castVote(Long voterUserId, String artistUserid, int cost) {
        User voter = userRepository.findById(voterUserId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        // 1. Check if user has enough coins
        if (voter.getCoinBalance() < cost) {
            throw new RuntimeException("Insufficient coin balance");
        }

        // 2. Deduct coins
        voter.setCoinBalance(voter.getCoinBalance() - cost);
        userRepository.save(voter);

        // 3. Record the vote
        Vote vote = new Vote();
        vote.setVoterUserId(voterUserId);
        vote.setArtistUserid(artistUserid);
        vote.setCost(cost);
        voteRepository.save(vote);
    }

    public int getCoinBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCoinBalance();
    }

    @Transactional
    public void addCoins(Long userId, int coinAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setCoinBalance(user.getCoinBalance() + coinAmount);
        userRepository.save(user);
    }
}
