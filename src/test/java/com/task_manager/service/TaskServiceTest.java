package com.task_manager.service;

import com.task_manager.model.Status;
import com.task_manager.model.Task;
import com.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Test Task 1");
        task1.setDescription("Test Description 1");
        task1.setStatus(Status.TODO);
        task1.setDueDate(LocalDate.now().plusDays(1));

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setDueDate(LocalDate.now().plusDays(2));
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        List<Task> expectedTasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.getAllTasks();

        assertThat(actualTasks).hasSize(2);
        assertThat(actualTasks).containsExactly(task1, task2);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasks_WhenNoTasks_ShouldReturnEmptyList() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList());

        List<Task> actualTasks = taskService.getAllTasks();

        assertThat(actualTasks).isEmpty();
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        Optional<Task> actualTask = taskService.getTaskById(1L);

        assertThat(actualTask).isPresent();
        assertThat(actualTask.get()).isEqualTo(task1);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WhenTaskNotFound_ShouldReturnEmpty() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Task> actualTask = taskService.getTaskById(999L);

        assertThat(actualTask).isNotPresent();
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");
        newTask.setStatus(Status.TODO);
        newTask.setDueDate(LocalDate.now().plusDays(3));

        Task savedTask = new Task();
        savedTask.setId(3L);
        savedTask.setTitle("New Task");
        savedTask.setDescription("New Description");
        savedTask.setStatus(Status.TODO);
        savedTask.setDueDate(LocalDate.now().plusDays(3));

        when(taskRepository.save(newTask)).thenReturn(savedTask);

        Task actualTask = taskService.createTask(newTask);

        assertThat(actualTask).isEqualTo(savedTask);
        assertThat(actualTask.getId()).isEqualTo(3L);
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        Task taskDetails = new Task();
        taskDetails.setTitle("Updated Task");
        taskDetails.setDescription("Updated Description");
        taskDetails.setStatus(Status.IN_PROGRESS);
        taskDetails.setDueDate(LocalDate.now().plusDays(5));

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Task");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(Status.TODO);
        existingTask.setDueDate(LocalDate.now().plusDays(1));

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Status.IN_PROGRESS);
        updatedTask.setDueDate(LocalDate.now().plusDays(5));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task actualTask = taskService.updateTask(1L, taskDetails);

        assertThat(actualTask.getId()).isEqualTo(1L);
        assertThat(actualTask.getTitle()).isEqualTo("Updated Task");
        assertThat(actualTask.getDescription()).isEqualTo("Updated Description");
        assertThat(actualTask.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(actualTask.getDueDate()).isEqualTo(LocalDate.now().plusDays(5));

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskNotFound_ShouldThrowRuntimeException() {
        Task taskDetails = new Task();
        taskDetails.setTitle("Updated Task");

        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(999L, taskDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task not found with id: 999");

        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldUpdateAllFields() {
        Task taskDetails = new Task();
        taskDetails.setTitle("New Title");
        taskDetails.setDescription("New Description");
        taskDetails.setStatus(Status.DONE);
        taskDetails.setDueDate(LocalDate.now().plusDays(7));

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(Status.TODO);
        existingTask.setDueDate(LocalDate.now().plusDays(1));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        taskService.updateTask(1L, taskDetails);

        assertThat(existingTask.getTitle()).isEqualTo("New Title");
        assertThat(existingTask.getDescription()).isEqualTo("New Description");
        assertThat(existingTask.getStatus()).isEqualTo(Status.DONE);
        assertThat(existingTask.getDueDate()).isEqualTo(LocalDate.now().plusDays(7));
    }

    @Test
    void updateTask_WithNullValues_ShouldSetFieldsToNull() {
        Task taskDetails = new Task();
        taskDetails.setTitle("Updated Title");
        taskDetails.setDescription(null);
        taskDetails.setStatus(null);
        taskDetails.setDueDate(null);

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(Status.TODO);
        existingTask.setDueDate(LocalDate.now().plusDays(1));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        taskService.updateTask(1L, taskDetails);

        assertThat(existingTask.getTitle()).isEqualTo("Updated Title");
        assertThat(existingTask.getDescription()).isNull();
        assertThat(existingTask.getStatus()).isNull();
        assertThat(existingTask.getDueDate()).isNull();
    }

    @Test
    void deleteTask_ShouldCallRepositoryDelete() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_WithNullId_ShouldCallRepositoryDelete() {
        doNothing().when(taskRepository).deleteById(null);

        taskService.deleteTask(null);

        verify(taskRepository, times(1)).deleteById(null);
    }

    @Test
    void deleteTask_WithNonExistentId_ShouldCallRepositoryDelete() {
        doNothing().when(taskRepository).deleteById(999L);

        taskService.deleteTask(999L);

        verify(taskRepository, times(1)).deleteById(999L);
    }
}