package com.singsation.repository;

import com.singsation.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.artistUserid = :artistUserid")
    long countVotesByArtistUserid(@Param("artistUserid") String artistUserid);

    // NEW: Group votes by artist
    @Query("SELECT v.artistUserid, COUNT(v) FROM Vote v GROUP BY v.artistUserid")
    List<Object[]> countVotesGroupedByArtist();

    // NEW: Get all votes for a specific artist
    List<Vote> findByArtistUserid(String artistUserid);

    // NEW: Sum total coins spent by a specific user on voting
    @Query("SELECT SUM(v.cost) FROM Vote v WHERE v.voterUserId = :userId")
    Long sumCostByVoterUserId(@Param("userId") Long userId);

    // NEW: Sum total coins spent across the entire platform
    @Query("SELECT SUM(v.cost) FROM Vote v")
    Long sumAllCosts();
}
