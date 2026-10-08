package com.shad0wowl.taskmanager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shad0wowl.taskmanager.model.User;
import com.shad0wowl.taskmanager.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override 
    public void run(String... args) throws Exception {

        User user = new User(
            "Test User",
            "test@example.com",
            "PASSWORD"
        );

        userRepository.save(user);
    }
}