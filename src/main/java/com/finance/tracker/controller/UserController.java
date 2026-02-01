package com.finance.tracker.controller;

import com.finance.tracker.dto.UserResponse;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/family")
    public List<UserResponse> getFamilyMembers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("PARENT".equals(currentUser.getRole())) {
            List<User> members = userRepository.findByFamilyId(currentUser.getFamilyId());
            return members.stream()
                    .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getRole()))
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(
                    new UserResponse(currentUser.getId(), currentUser.getUsername(), currentUser.getRole())
            );
        }
    }
}