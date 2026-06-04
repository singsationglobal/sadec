package com.singsation.service;

import com.singsation.model.User;
import com.singsation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

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

    // ADD THIS METHOD - RESET COMPETITION ENTRY
    @Transactional
    public void resetCompetitionEntry(@NonNull Long id) {
        User user = getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setHasCompletedEntry(false);
        userRepository.save(user);
        System.out.println("Reset competition entry for user: " + user.getEmail());
    }
}