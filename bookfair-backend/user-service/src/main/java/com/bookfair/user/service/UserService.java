package com.bookfair.user.service;

import com.bookfair.user.dto.AuthResponse;
import com.bookfair.user.dto.LoginRequest;
import com.bookfair.user.dto.RegisterRequest;
import com.bookfair.user.dto.UserResponse;
import com.bookfair.user.entity.User;
import com.bookfair.user.entity.UserRole;
import com.bookfair.user.repository.UserRepository;
import com.bookfair.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBusinessName(request.getBusinessName());
        user.setContactPerson(request.getContactPerson());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(UserRole.VENDOR);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return new AuthResponse(token, mapToUserResponse(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, mapToUserResponse(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id, String currentUserEmail) {
        User requestedUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usre not found"));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException(" user not found"));

        if (!requestedUser.getEmail().equals(currentUserEmail) && 
            !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new RuntimeException("Unauthorized");
        }
        
        return mapToUserResponse(requestedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setBusinessName(user.getBusinessName());
        response.setContactPerson(user.getContactPerson());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}