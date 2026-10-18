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
        Page<User> users = userRepository.findAllActiveUsers(pageable);
        System.out.println("Active users found: " + users.getTotalElements());
        return users;
    }

    public Optional<User> getUserById(@NonNull Long id) {
        return userRepository.findById(id);
    }

    public long getTotalUsersCount() {
        long count = userRepository.countByIsDeletedFalse();
        System.out.println("=== getTotalUsersCount (Active): " + count);
        return count;
    }

    @Transactional
    public void softDeleteUser(@NonNull Long id) {
        User user = getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void restoreUser(@NonNull Long id) {
        User user = getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(false);
        user.setDeletedAt(null);
        userRepository.save(user);
    }

    @Transactional
    public void permanentDeleteUser(@NonNull Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
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
    }
    
    public Page<User> searchUsers(@NonNull String query, @NonNull Pageable pageable) {
        System.out.println("=== AdminUserService.searchUsers CALLED with query: " + query);
        return userRepository.searchUsers(query, pageable);
    }

    @Transactional
    public void sendMessageToUser(@NonNull Long userId, @NonNull String messageType, @NonNull String messageContent) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Complaint adminMessage = new Complaint();
        adminMessage.setUser(user);
        adminMessage.setSubject("Message from Admin: " + messageType);
        adminMessage.setMessage(messageContent);
        adminMessage.setStatus("OPEN");
        adminMessage.setAdminReply(null);
        adminMessage.setCreatedAt(LocalDateTime.now());
        adminMessage.setUpdatedAt(LocalDateTime.now());
        
        complaintRepository.save(adminMessage);
        System.out.println("=== Admin message saved to complaints for User ID: " + userId + " | Type: " + messageType);
    }
}
