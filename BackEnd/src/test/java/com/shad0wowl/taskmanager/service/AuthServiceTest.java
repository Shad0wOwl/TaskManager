package com.shad0wowl.taskmanager.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.shad0wowl.taskmanager.repository.UserRepository;
import com.shad0wowl.taskmanager.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterNewUser() {

        String name = "Test";
        String email = "test@test.com";
        String password = "12345678";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        authService.register(name, email, password);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User saveUser = userCaptor.getValue();

        assertEquals(name, saveUser.getName());
        assertEquals(email, saveUser.getEmail());
        assertNotEquals(password, saveUser.getPassword());
    }

    @Test 
    void shouldRejectDuplicateEmail() {

        String email = "test@test.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authService.register("Test", email, "12345678")
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginWithValidCredentials() {

        String email = "test@test.com";
        String password = "12345678";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        User user = new User("Test", email, hashedPassword);

        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));
        
        UserDetails result = authService.login(email, password);

        assertEquals(email, result.getUsername());
        assertTrue(encoder.matches(password, result.getPassword()));

        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldRejectInvalidPassword() {

        String email = "test@test.com";
        String correctPassword = "12345678";
        String wrongPassword = "wrongpassword";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(correctPassword);

        User user = new User("Test", email, hashedPassword);

        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authService.login(email, wrongPassword)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldRejectNonExistenUser() {

        String email = "unknown@test.com";
        String password = "12345678";

        when(userRepository.findByEmail(email))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authService.login(email, password)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(userRepository).findByEmail(email);
    }
}
