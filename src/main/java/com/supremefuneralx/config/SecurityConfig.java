package com.supremefuneralx.config;

import com.supremefuneralx.security.JwtAuthenticationFilter;
import com.supremefuneralx.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

/**
 * Security configuration for the Inventory Service.
 * 
 * This configuration:
 * - Implements JWT-based authentication for API security
 * - Enables CORS with specific allowed origins only
 * - Disables CSRF (acceptable for stateless REST API)
 * - Sets secure headers for protection against common attacks (XSS,
 * clickjacking)
 * - Protects sensitive endpoints with @PreAuthorize annotations
 * - Allows public access to login and health check endpoints
 * 
 * Token Flow:
 * 1. Client sends credentials to POST /api/v1/auth/login
 * 2. Server validates credentials and returns JWT token
 * 3. Client includes token in Authorization header: Bearer {token}
 * 4. JwtAuthenticationFilter validates token and sets authentication
 * 5. Protected endpoints check authentication via @PreAuthorize
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider)
            throws Exception {
        http
                // Disable CSRF for stateless API (JWT tokens provide protection)
                .csrf(csrf -> csrf.disable())
                // Use stateless sessions (JWT-based)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Set CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Security headers for XSS, clickjacking, MIME sniffing protection
                .headers(headers -> {
                    headers.xssProtection();
                    headers.frameOptions(frameOptions -> frameOptions.deny());
                    headers.contentTypeOptions();
                    headers.cacheControl();
                })
                // Configure API authorization
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/actuator/**", "/health").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Protected inventory endpoints - require JWT authentication
                        .requestMatchers("/api/v1/items/**").authenticated()
                        .requestMatchers("/api/v1/suppliers/**").authenticated()
                        .requestMatchers("/api/v1/additions/**").authenticated()
                        .requestMatchers("/api/v1/issues/**").authenticated()
                        // All other requests require authentication
                        .anyRequest().authenticated())
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Only allow specific origins - never use wildcards with credentials
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://localhost:8081",
                "http://localhost:8084",
                "https://inventory-sfd.vercel.app",
                "https://helpful-cooperation-production-278d.up.railway.app"));
        // Only allow necessary HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Only allow specific headers - never use wildcard (includes Authorization for
        // JWT)
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setExposedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt with strength 12 (stronger than default 10)
        return new BCryptPasswordEncoder(12);
    }
}
