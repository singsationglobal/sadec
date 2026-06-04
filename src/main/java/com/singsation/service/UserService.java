package com.singsation.service;

import com.singsation.model.User;
import com.singsation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        if (user.getContact() != null && !user.getContact().contains("@")) {
            if (userRepository.findByContact(user.getContact()).isPresent()) {
                throw new RuntimeException("Phone number already registered");
            }
        }
        
        // ✅ CASE-INSENSITIVE USERID CHECK
        if (user.getUserid() != null && !user.getUserid().isEmpty()) {
            Optional<User> existingByUserid = userRepository.findByUseridIgnoreCase(user.getUserid());
            if (existingByUserid.isPresent()) {
                throw new RuntimeException("User ID '" + user.getUserid() + "' is already taken. Please choose another.");
            }
        } else {
            String newUserid;
            do {
                newUserid = "SING_" + System.currentTimeMillis();
            } while (userRepository.findByUseridIgnoreCase(newUserid).isPresent());
            user.setUserid(newUserid);
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByContact(String contact) {
        return userRepository.findByContact(contact);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUserid(String userid) {
        return userRepository.findByUserid(userid);
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ✅ CASE-INSENSITIVE USERID CHECK
        if (userDetails.getUserid() != null && !userDetails.getUserid().isEmpty() 
            && !userDetails.getUserid().equalsIgnoreCase(user.getUserid())) {
            
            Optional<User> existingUser = userRepository.findByUseridIgnoreCase(userDetails.getUserid());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new RuntimeException("User ID '" + userDetails.getUserid() + "' is already taken. Please choose another.");
            }
            user.setUserid(userDetails.getUserid());
        }
        
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getSurname() != null) user.setSurname(userDetails.getSurname());
        if (userDetails.getContact() != null) user.setContact(userDetails.getContact());
        if (userDetails.getWinner() != null) user.setWinner(userDetails.getWinner());
        if (userDetails.getAlternativeContact() != null) {
            user.setAlternativeContact(userDetails.getAlternativeContact());
        }
        if (userDetails.getProvince() != null) {
            user.setProvince(userDetails.getProvince());
        }
        if (userDetails.getAge() != null) {
            user.setAge(userDetails.getAge());
        }
        
        user.setHasCompletedEntry(userDetails.isHasCompletedEntry());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUserFromMap(Long userId, Map<String, Object> userDetails) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ✅ CASE-INSENSITIVE USERID CHECK
        if (userDetails.containsKey("userid")) {
            String newUserid = userDetails.get("userid").toString();
            if (!newUserid.isEmpty() && !newUserid.equalsIgnoreCase(user.getUserid())) {
                Optional<User> existing = userRepository.findByUseridIgnoreCase(newUserid);
                if (existing.isPresent() && !existing.get().getId().equals(userId)) {
                    throw new RuntimeException("User ID '" + newUserid + "' is already taken. Please choose another.");
                }
                user.setUserid(newUserid);
            }
        }
        
        if (userDetails.containsKey("name")) {
            user.setName(userDetails.get("name").toString());
        }
        if (userDetails.containsKey("surname")) {
            user.setSurname(userDetails.get("surname").toString());
        }
        if (userDetails.containsKey("province")) {
            user.setProvince(userDetails.get("province").toString());
        }
        if (userDetails.containsKey("age")) {
            try {
                user.setAge(Integer.parseInt(userDetails.get("age").toString()));
            } catch (NumberFormatException e) {
                // Ignore invalid age
            }
        }
        if (userDetails.containsKey("alternativeContact")) {
            user.setAlternativeContact(userDetails.get("alternativeContact").toString());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUserFromCompetition(Long userId, String newUserid, String artistName, 
                                          String alternativeContact, boolean hasCompletedEntry) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ✅ CASE-INSENSITIVE USERID CHECK
        if (newUserid != null && !newUserid.isEmpty() && !newUserid.equalsIgnoreCase(user.getUserid())) {
            Optional<User> existing = userRepository.findByUseridIgnoreCase(newUserid);
            if (existing.isPresent() && !existing.get().getId().equals(userId)) {
                throw new RuntimeException("User ID '" + newUserid + "' is already taken. Please choose another.");
            }
            user.setUserid(newUserid);
        }
        
        if (artistName != null) user.setName(artistName);
        if (alternativeContact != null) user.setAlternativeContact(alternativeContact);
        user.setHasCompletedEntry(hasCompletedEntry);
        
        return userRepository.save(user);
    }
    
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Transactional
    public void deleteUserAccount(Long userId, String password) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        userRepository.delete(user);
    }
    
    @Transactional
    public void updateUserPassword(User user) {
        userRepository.save(user);
    }
}