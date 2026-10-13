package com.shad0wowl.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.shad0wowl.taskmanager.repository.TaskRepository;
import com.shad0wowl.taskmanager.repository.UserRepository;
import com.shad0wowl.taskmanager.model.Task;
import com.shad0wowl.taskmanager.model.User;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(String title, String description, String email) {

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"
            ));
        
        Task task = new Task(title, description);
        task.setUser(user);

        return taskRepository.save(task);
    }

    public List<Task> getTasks(String email) {
        return taskRepository.findByUserEmail(email);
    }

    public Task updateTaskStatus(Long taskId, boolean completed, String email) {

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Task not found"
            ));
        
        if (!task.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Access denied"
            );
        }

        task.setCompleted(completed);

        return taskRepository.save(task);
    }
}
