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
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
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
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Truly public endpoints
                .requestMatchers("/", "/favicon.ico", "/health", "/error", "/actuator/health").permitAll()
                
                // 2. Auth endpoints (login, register, OTP) are public
                .requestMatchers("/api/auth/**").permitAll()
                
                // 3. Public read-only data (songs, winners, splash, webhooks)
                .requestMatchers("/api/songs", "/api/songs/**").permitAll()
                .requestMatchers("/api/splash-screen").permitAll()
                .requestMatchers("/api/winners/active").permitAll()
                .requestMatchers("/api/webhooks/yoco/**").permitAll()
                
                // 4. Admin Login is public
                .requestMatchers("/api/admin/auth/login").permitAll()

                // 5. Admin Panel (Requires Roles)
                .requestMatchers("/api/admin/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/songs/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/splash-screen/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/winners/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/staff/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/logs/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")
                .requestMatchers("/api/admin/user-activity/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")
                .requestMatchers("/api/admin/complaints/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR", "SUPPORT")
                .requestMatchers("/api/admin/payments/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")
                .requestMatchers("/api/admin/dashboard/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR", "SUPPORT")
                .requestMatchers("/api/admin/voting/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MODERATOR")

                // 6. Karaoke App User Endpoints (MUST BE AUTHENTICATED)
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/competitions/**").authenticated()
                .requestMatchers("/api/complaints/**").authenticated()
                .requestMatchers("/api/payments/**").authenticated()
                .requestMatchers("/api/votes/**").authenticated()

                // 7. Everything else requires a login
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
