package com.singsation.repository;

import com.singsation.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByContact(String contact);
    Optional<User> findByUserid(String userid);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.userid) = LOWER(:userid)")
    Optional<User> findByUseridIgnoreCase(@Param("userid") String userid);
    
    Optional<User> findByAlternativeContact(String alternativeContact);
    
    // ✅ UPDATED: Only search non-deleted users
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND (" +
           "LOWER(u.userid) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.contact) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    // ✅ ADDED: Count only non-deleted users for accurate Dashboard Stats
    long countByIsDeletedFalse();

    // ✅ ADDED: Fetch only non-deleted users for the User Management screen
    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    Page<User> findAllActiveUsers(Pageable pageable);

    // ✅ ADDED: Fetch deleted users for a potential User Recycle Bin
    @Query("SELECT u FROM User u WHERE u.isDeleted = true")
    Page<User> findAllDeletedUsers(Pageable pageable);
}
