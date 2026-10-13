package com.shad0wowl.taskmanager.dto;

import jakarta.validation.constraints.NotNull;

public record TaskStatusRequest(
    @NotNull Boolean completed
) {}