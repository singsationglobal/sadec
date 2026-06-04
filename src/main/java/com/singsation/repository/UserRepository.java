package com.singsation.repository;

import com.singsation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByContact(String contact);
    Optional<User> findByUserid(String userid);
    boolean existsByEmail(String email);
    
    // Case-insensitive UserID search
    @Query("SELECT u FROM User u WHERE LOWER(u.userid) = LOWER(:userid)")
    Optional<User> findByUseridIgnoreCase(@Param("userid") String userid);
    
    // NEW: Find user by their 2-way authentication contact
    Optional<User> findByAlternativeContact(String alternativeContact);
}