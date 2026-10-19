package com.shad0wowl.taskmanager.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shad0wowl.taskmanager.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public Map<String, String> getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Not authenticated"
            );
        }

        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
            ));
        
        return Map.of(
            "name", user.getName(),
            "email", user.getEmail()
        );
    }
}
