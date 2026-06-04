package com.singsation.security;

import com.singsation.model.Admin;
import com.singsation.model.AdminRole;
import com.singsation.model.User;
import com.singsation.repository.AdminRepository;
import com.singsation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user by email: " + username);
        
        // FIRST: Check if this is an admin user
        Optional<Admin> adminOpt = adminRepository.findByEmail(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            AdminRole role = admin.getRole();
            String roleName = role != null ? role.getName() : "SUPPORT";
            
            System.out.println("Found ADMIN user: " + admin.getEmail() + " with role: " + roleName);
            
            return org.springframework.security.core.userdetails.User
                .withUsername(admin.getEmail())
                .password(admin.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName)))
                .build();
        }
        
        // SECOND: Check regular users
        Optional<User> userOpt = userRepository.findByEmail(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Found regular USER: " + user.getEmail());
            
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        }
        
        System.out.println("User not found with email: " + username);
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}