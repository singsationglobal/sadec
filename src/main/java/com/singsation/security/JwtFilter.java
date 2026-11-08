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

    // ✅ FIXED: Removed wildcards that were accidentally exposing user routes
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/",
        "/favicon.ico",
        "/health",
        "/api/auth/",
        "/api/webhooks/yoco/",
        "/actuator/health",
        "/api/songs",
        "/api/splash-screen",
        "/api/winners/active",
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

        // Check if this is a public path (Strict matching now)
        boolean isPublic = false;
        for (String publicPath : PUBLIC_PATHS) {
            if (publicPath.equals("/") && path.equals("/")) {
                isPublic = true;
                break;
            }
            if (!publicPath.equals("/") && path.startsWith(publicPath)) {
                isPublic = true;
                break;
            }
        }

        if (isPublic) {
            System.out.println("PUBLIC path - skipping JWT: " + path);
            chain.doFilter(request, response);
            return;
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
        
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("✅ Authentication set successfully for: " + username);
        } else {
            System.out.println("❌ JWT validation FAILED - Invalid token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        chain.doFilter(request, response);
    }
}
