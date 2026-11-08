package com.singsation.repository;

import com.singsation.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    // Sum of coins received by an artist (was: count of votes)
    @Query("SELECT COALESCE(SUM(v.cost), 0) FROM Vote v WHERE v.artistUserid = :artistUserid")
    long countVotesByArtistUserid(@Param("artistUserid") String artistUserid);

    // Group coins received by artist
    @Query("SELECT v.artistUserid, COALESCE(SUM(v.cost), 0) FROM Vote v GROUP BY v.artistUserid")
    List<Object[]> countVotesGroupedByArtist();

    // All vote rows for a specific artist (unchanged)
    List<Vote> findByArtistUserid(String artistUserid);

    // Sum total coins spent by a specific user on voting (unchanged)
    @Query("SELECT SUM(v.cost) FROM Vote v WHERE v.voterUserId = :userId")
    Long sumCostByVoterUserId(@Param("userId") Long userId);

    // Sum total coins spent across the entire platform (unchanged)
    @Query("SELECT SUM(v.cost) FROM Vote v")
    Long sumAllCosts();
}
