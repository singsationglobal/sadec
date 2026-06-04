package com.singsation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    // PUBLIC paths that don't require JWT
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register", 
        "/api/auth/send-sms-otp",
        "/api/auth/verify-sms-otp",
        "/api/auth/send-email-otp",
        "/api/auth/verify-email-otp",
        "/api/auth/reset-password-with-sms",
        "/api/auth/reset-password-with-email",
        "/api/auth/verify-signup-otp",
        "/api/webhooks/yoco",
        "/api/songs",
        "/api/splash-screen",
        "/api/winners/active",
        "/actuator/health",
        "/api/admin/auth/login",
        "/error"
    );
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("=== JWT FILTER DEBUG ===");
        System.out.println("Request: " + method + " " + path);
        
        // Always allow OPTIONS preflight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("OPTIONS preflight - skipping JWT");
            chain.doFilter(request, response);
            return;
        }
        
        // Check if this is a public path
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                System.out.println("PUBLIC path - skipping JWT: " + path);
                chain.doFilter(request, response);
                return;
            }
        }
        
        // ALL other paths require JWT validation
        System.out.println("PROTECTED path - requiring JWT: " + path);
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null) {
            System.out.println("❌ No Authorization header found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header required");
            return;
        }
        
        if (!authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Authorization header does not start with Bearer");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bearer token required");
            return;
        }
        
        String token = authHeader.substring(7);
        System.out.println("✅ Token found, length: " + token.length());
        System.out.println("Token first 20 chars: " + token.substring(0, Math.min(20, token.length())) + "...");
        
        // Validate the token
        boolean isValid = jwtUtil.validateToken(token);
        System.out.println("Token validation result: " + isValid);
        
        if (isValid) {
            String username = jwtUtil.getUsernameFromToken(token);
            System.out.println("Username from token: " + username);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("UserDetails loaded: " + userDetails.getUsername());
            System.out.println("Authorities: " + userDetails.getAuthorities());
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            System.out.println("✅ Authentication set successfully");
            request.setAttribute("username", username);
        } else {
            System.out.println("❌ JWT validation FAILED - Invalid token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }
        
        chain.doFilter(request, response);
    }
}