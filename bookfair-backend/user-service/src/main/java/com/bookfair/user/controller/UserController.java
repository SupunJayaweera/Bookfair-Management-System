package com.bookfair.user.controller;

import com.bookfair.user.dto.AuthResponse;
import com.bookfair.user.dto.ErrorResponse;
import com.bookfair.user.dto.LoginRequest;
import com.bookfair.user.dto.RegisterRequest;
import com.bookfair.user.dto.UserResponse;
import com.bookfair.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Email already registered")) {
                ErrorResponse errorResponse = new ErrorResponse("Email already exists: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
            ErrorResponse errorResponse = new ErrorResponse(errorMessage != null ? errorMessage : "Registration failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Invalid credentials")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invald email or password"));
            }
            if (errorMessage != null && errorMessage.contains("Account is deactivated")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Account is deactivated. Please contact support."));
            }
            ErrorResponse errorResponse = new ErrorResponse(errorMessage != null ? errorMessage : "Login failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Authentication needed"));
            }
            
            String email = authentication.getName();
            UserResponse response = userService.getCurrentUser(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Authentication needed"));
            }
            
            String currentUserEmail = authentication.getName();
            UserResponse response = userService.getUserById(id, currentUserEmail);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(errorMessage));
            }
            ErrorResponse errorResponse = new ErrorResponse(errorMessage != null ? errorMessage : "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
