package com.finance.tracker.service;

import com.finance.tracker.dto.AuthResponse;
import com.finance.tracker.dto.LoginRequest;
import com.finance.tracker.dto.RegisterRequest;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        String role = (request.getRole() != null) ? request.getRole().toUpperCase() : "CHILD";
        user.setRole(role);

        if ("PARENT".equals(role)) {
            String newFamilyCode = "FAM-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            user.setFamilyId(newFamilyCode);
        } else {
            if (request.getFamilyId() == null || request.getFamilyId().isEmpty()) {
                throw new RuntimeException("Family ID is required for child role");
            }
            user.setFamilyId(request.getFamilyId());
        }

        userRepository.save(user);

        if ("PARENT".equals(role)) {
            new Thread(() -> {
                emailService.sendFamilyCode(user.getEmail(), user.getUsername(), user.getFamilyId());
            }).start();
        }

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(authentication);

        return new AuthResponse(token);
    }
}