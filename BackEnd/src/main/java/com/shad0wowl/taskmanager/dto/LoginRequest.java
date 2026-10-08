package com.shad0wowl.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email adress")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {}
