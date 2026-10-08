package com.shad0wowl.taskmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.shad0wowl.taskmanager.repository.UserRepository;
import com.shad0wowl.taskmanager.model.User;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email is already registered"
            );
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(name, email, hashedPassword);

        userRepository.save(user);
    }

    public void login(String email, String password) {

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
            ));
        
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
                );
            }
    }
}
