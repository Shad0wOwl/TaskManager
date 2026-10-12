package com.shad0wowl.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
    @NotBlank String title,
    String description
) {}
