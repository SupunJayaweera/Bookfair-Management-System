package com.bookfair.user.controller;

import com.bookfair.user.dto.AuthResponse;
import com.bookfair.user.dto.ErrorResponse;
import com.bookfair.user.dto.RegisterRequest;
import com.bookfair.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
