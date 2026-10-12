package com.shad0wowl.taskmanager.dto;

import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt
) {}