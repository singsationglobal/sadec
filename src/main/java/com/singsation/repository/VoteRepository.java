package com.singsation.repository;

import com.singsation.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.artistUserid = :artistUserid")
    long countVotesByArtistUserid(@Param("artistUserid") String artistUserid);
}
