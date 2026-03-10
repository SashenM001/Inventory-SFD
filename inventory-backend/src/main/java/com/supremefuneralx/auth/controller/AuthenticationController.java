package com.supremefuneralx.auth.controller;

import com.supremefuneralx.auth.dto.LoginRequest;
import com.supremefuneralx.auth.dto.LoginResponse;
import com.supremefuneralx.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication Controller
 * 
 * Provides login/authentication endpoints for JWT token generation.
 * 
 * Endpoints:
 * - POST /api/v1/auth/login - Authenticate user and get JWT token
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Login - Generate JWT token for authenticated user
     * 
     * @param loginRequest Contains username and password
     * @return JWT token in response
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.debug("Login attempt for user: {}", loginRequest.getUsername());

            // Authenticate user with credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Generate JWT token
            String jwtToken = jwtTokenProvider.generateToken(authentication);

            log.info("User authenticated successfully: {}", loginRequest.getUsername());

            return ResponseEntity.ok(new LoginResponse(jwtToken, "Bearer", 86400L));

        } catch (BadCredentialsException ex) {
            log.warn("Login failed - Invalid credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password"));

        } catch (Exception ex) {
            log.error("Login error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Authentication failed: " + ex.getMessage()));
        }
    }

    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
