package com.shad0wowl.taskmanager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import com.shad0wowl.taskmanager.service.TaskService;
import com.shad0wowl.taskmanager.model.Task;
import com.shad0wowl.taskmanager.dto.TaskRequest;
import com.shad0wowl.taskmanager.dto.TaskResponse;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse createTask(
        @Valid @RequestBody TaskRequest request,
        Authentication authentication
    ) {
        Task task = taskService.createTask(
            request.title(),
            request.description(),
            authentication.getName()
        );

        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getCreatedAt()
        );
    }

    @GetMapping
    public List<TaskResponse> getTask(Authentication authentication) {

        return taskService.getTasks(authentication.getName())
            .stream()
            .map(task -> new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt()
            ))
            .toList();
    }
}