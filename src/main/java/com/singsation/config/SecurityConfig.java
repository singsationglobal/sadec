package com.singsation.config;

import com.singsation.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication needed)
                .requestMatchers(
                    "/api/auth/**",
                    "/api/webhooks/yoco/**",
                    "/actuator/health",
                    "/api/songs",
                    "/api/songs/**",
                    "/api/splash-screen",
                    "/api/winners/active",
                    "/error"
                ).permitAll()
                
                // Admin Auth - public
                .requestMatchers("/api/admin/auth/login").permitAll()
                
                // ===== ADMIN ENDPOINTS WITH ROLE RESTRICTIONS =====
                // SUPER_ADMIN and ADMIN only
                .requestMatchers("/api/admin/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/songs/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/splash-screen/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/winners/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/staff/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                
                // SUPER_ADMIN, ADMIN, MODERATOR can view logs and user activity
                .requestMatchers("/api/admin/logs/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")
                .requestMatchers("/api/admin/user-activity/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")
                
                // SUPER_ADMIN, ADMIN, MODERATOR, SUPPORT can view complaints
                .requestMatchers("/api/admin/complaints/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR", "SUPPORT")
                
                // SUPER_ADMIN, ADMIN, MODERATOR can view payments
                .requestMatchers("/api/admin/payments/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")
                
                // Dashboard - all admin roles can view
                .requestMatchers("/api/admin/dashboard/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR", "SUPPORT")
                
                // ===== USER ENDPOINTS =====
                .requestMatchers(
                    "/api/users/**",
                    "/api/competitions/**",
                    "/api/complaints/**",
                    "/api/payments/**"
                ).authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}