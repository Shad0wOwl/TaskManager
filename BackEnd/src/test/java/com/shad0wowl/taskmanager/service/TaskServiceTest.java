package com.shad0wowl.taskmanager.service;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.shad0wowl.taskmanager.model.Task;
import com.shad0wowl.taskmanager.model.User;
import com.shad0wowl.taskmanager.repository.TaskRepository;
import com.shad0wowl.taskmanager.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskForUser() {

        String email = "test@test.com";
        String title = "JUnit Test";
        String description = "Write tests";

        User user = new User("Test", email, "hashedPassword");

        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));

        taskService.createTask(title, description, email);

        ArgumentCaptor<Task> taskCaptor =
            ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();

        assertEquals(title, savedTask.getTitle());
        assertEquals(description, savedTask.getDescription());
        assertEquals(user, savedTask.getUser());
    }

    @Test
    void shouldRejectTaskCreationForNonExistentUser() {

        String email = "unknown@test.com";

        when(userRepository.findByEmail(email))
            .thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> taskService.createTask(
                "New Task",
                "Task description",
                email
            )
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldGetTasksForUser() {

        String email = "test@test.com";

        Task task1 = new Task("Task1", "Prepare for trouble");
        Task task2 = new Task("Task2", "Make it double");

        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.findByUserEmail(email))
            .thenReturn(tasks);

        List<Task> result = taskService.getTasks(email);

        assertEquals(2, result.size());
        assertEquals(task1, result.get(0));
        assertEquals(task2, result.get(1));

        verify(taskRepository).findByUserEmail(email);
    }

    @Test
    void shouldUpdateTaskStatus() {

        Long taskId = 1L;
        String email = "test@test.com";

        User user = new User("Test", email, "hashedPassword");
        Task task = new Task("Another", "One is not enought");

        task.setUser(user);
        task.setCompleted(false);

        when(taskRepository.findById(taskId))
            .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
            .thenReturn(task);

        Task result = taskService.updateTaskStatus(taskId, true, email);

        assertTrue(result.isCompleted());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void shouldRejectStatusUpdateForAnotherUser() {

        Long taskId = 1L;
        String ownerEmail = "owner@test.com";
        String otherEmail = "other@test.com";

        User owner = new User("Owner", ownerEmail, "hashedPassword");
        Task task = new Task("Task", "Only owner can edit");

        task.setUser(owner);
        task.setCompleted(false);

        when(taskRepository.findById(taskId))
            .thenReturn(Optional.of(task));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> taskService.updateTaskStatus(taskId, true, otherEmail)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertFalse(task.isCompleted());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteOwnTask() {
        
        Long taskId = 1L;
        String email = "test@test.com";

        User user = new User("Test", email, "hashedPassword");
        Task task = new Task("Task", "Description");

        task.setUser(user);

        when(taskRepository.findById(taskId))
            .thenReturn(Optional.of(task));

        taskService.deleteTask(taskId, email);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldRejectDeletingAnotherUserTask() {

        Long taskId = 1L;
        String ownerEmail = "owner@test.com";
        String otherEmail = "other@test.com";

        User owner = new User("Owner", ownerEmail, "hashedPassword");
        Task task = new Task("Task", "Only owner can delete");

        task.setUser(owner);

        when(taskRepository.findById(taskId))
            .thenReturn(Optional.of(task));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> taskService.deleteTask(taskId, otherEmail)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void shouldUpdateOwnTask() {

        Long taskId = 1L;
        String email = "test@test.com";

        User user = new User("Test", email, "hashedPassword");
        Task task = new Task("Old Title", "Old description");

        task.setUser(user);

        when(taskRepository.findById(taskId))
            .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
            .thenReturn(task);

        Task result = taskService.updateTask(
            taskId,
            "New Title",
            "New Description",
            email
        );

        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void shouldRejectUpdatingAnotherUsersTask() {
    
        Long taskId = 1L;
        String ownerEmail = "owner@test.com";
        String otherEmail = "other@test.com";

        User owner = new User("Owner", ownerEmail, "hashedPassword");
        Task task = new Task("Original title", "Original description");

        task.setUser(owner);

        when(taskRepository.findById(taskId))
            .thenReturn(Optional.of(task));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> taskService.updateTask(
                taskId,
                "Hacked title",
                "Hacked description",
                otherEmail
            )
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        assertEquals("Original title", task.getTitle());
        assertEquals("Original description", task.getDescription());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldRejectUpdatingNonExistentTask() {

        Long taksId = 999L;
        String email = "test@test.com";

        when(taskRepository.findById(taksId))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> taskService.updateTask(taksId,
                "New Title",
                "New Description",
                email
            )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(taskRepository).findById(taksId);
        verify(taskRepository, never()).save(any(Task.class));
    }
}
