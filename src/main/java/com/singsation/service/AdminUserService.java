package com.singsation.service;

import com.singsation.model.Complaint;
import com.singsation.model.User;
import com.singsation.repository.ComplaintRepository;
import com.singsation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    public Page<User> getAllUsers(@NonNull Pageable pageable) {
        System.out.println("=== AdminUserService.getAllUsers CALLED ===");
        Page<User> users = userRepository.findAll(pageable);
        System.out.println("Users found: " + users.getTotalElements());
        return users;
    }

    public Optional<User> getUserById(@NonNull Long id) {
        return userRepository.findById(id);
    }

    public long getTotalUsersCount() {
        long count = userRepository.count();
        System.out.println("=== getTotalUsersCount: " + count);
        return count;
    }

    @Transactional
    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void banUser(@NonNull Long id) {
        User user = getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(@NonNull Long id) {
        User user = getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void resetCompetitionEntry(@NonNull Long id) {
        User user = getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setHasCompletedEntry(false);
        userRepository.save(user);
        System.out.println("Reset competition entry for user: " + user.getEmail());
    }
    
    public Page<User> searchUsers(@NonNull String query, @NonNull Pageable pageable) {
        System.out.println("=== AdminUserService.searchUsers CALLED with query: " + query);
        return userRepository.searchUsers(query, pageable);
    }

    // ✅ FIXED: Removed setIsDeleted to prevent compilation error. 
    // The database defaults 'is_deleted' to 0 automatically.
    @Transactional
    public void sendMessageToUser(@NonNull Long userId, @NonNull String messageType, @NonNull String messageContent) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Complaint adminMessage = new Complaint();
        adminMessage.setUser(user);
        adminMessage.setSubject("Message from Admin: " + messageType);
        adminMessage.setMessage(messageContent);
        adminMessage.setStatus("OPEN"); // Shows as a new ticket for the user
        adminMessage.setAdminReply(null); // User can reply to this later
        
        // Note: created_at and updated_at are often handled by @CreationTimestamp/@UpdateTimestamp 
        // or DB defaults, but we set them explicitly just in case your model requires it.
        adminMessage.setCreatedAt(LocalDateTime.now());
        adminMessage.setUpdatedAt(LocalDateTime.now());
        
        complaintRepository.save(adminMessage);
        System.out.println("=== Admin message saved to complaints for User ID: " + userId + " | Type: " + messageType);
    }
}
