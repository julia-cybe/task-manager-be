package com.task_manager.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task_manager.dto.TaskDTO;
import com.task_manager.model.Status;
import com.task_manager.model.Task;
import com.task_manager.service.TaskService;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task1;
    private Task task2;
    private TaskDTO taskDTO1;
    private TaskDTO taskDTO2;

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

        taskDTO1 = new TaskDTO(1L, "Test Task 1", "Test Description 1", Status.TODO, LocalDate.now().plusDays(1));
        taskDTO2 = new TaskDTO(2L, "Test Task 2", "Test Description 2", Status.IN_PROGRESS, LocalDate.now().plusDays(2));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task 1"))
                .andExpect(jsonPath("$[0].description").value("Test Description 1"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Test Task 2"))
                .andExpect(jsonPath("$[1].description").value("Test Description 2"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    void getAllTasks_WhenNoTasks_ShouldReturnEmptyList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));

        mockMvc.perform(get("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task 1"))
                .andExpect(jsonPath("$.description").value("Test Description 1"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void getTaskById_WhenTaskNotFound_ShouldReturn404() throws Exception {
        when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_WithValidData_ShouldReturnCreatedTask() throws Exception {
        TaskDTO newTaskDTO = new TaskDTO(null, "New Task", "New Description", Status.TODO, LocalDate.now().plusDays(3));
        Task newTask = new Task();
        newTask.setId(3L);
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");
        newTask.setStatus(Status.TODO);
        newTask.setDueDate(LocalDate.now().plusDays(3));

        when(taskService.createTask(any(Task.class))).thenReturn(newTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void createTask_WithBlankTitle_ShouldReturn400() throws Exception {
        TaskDTO invalidTaskDTO = new TaskDTO(null, "", "New Description", Status.TODO, LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTaskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_WithNullTitle_ShouldReturn400() throws Exception {
        TaskDTO invalidTaskDTO = new TaskDTO(null, null, "New Description", Status.TODO, LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTaskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_WithTitleTooLong_ShouldReturn400() throws Exception {
        String longTitle = "a".repeat(101);
        TaskDTO invalidTaskDTO = new TaskDTO(null, longTitle, "New Description", Status.TODO, LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTaskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_WithDescriptionTooLong_ShouldReturn400() throws Exception {
        String longDescription = "a".repeat(501);
        TaskDTO invalidTaskDTO = new TaskDTO(null, "Valid Title", longDescription, Status.TODO, LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTaskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_WithValidData_ShouldReturnUpdatedTask() throws Exception {
        TaskDTO updateTaskDTO = new TaskDTO(1L, "Updated Task", "Updated Description", Status.IN_PROGRESS, LocalDate.now().plusDays(5));
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Status.IN_PROGRESS);
        updatedTask.setDueDate(LocalDate.now().plusDays(5));

        when(taskService.updateTask(anyLong(), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTask_WithInvalidTitle_ShouldReturn400() throws Exception {
        TaskDTO invalidTaskDTO = new TaskDTO(1L, "", "Updated Description", Status.IN_PROGRESS, LocalDate.now().plusDays(5));

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTaskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_WhenTaskNotFound_ShouldReturn404() throws Exception {
        TaskDTO updateTaskDTO = new TaskDTO(999L, "Updated Task", "Updated Description", Status.IN_PROGRESS, LocalDate.now().plusDays(5));

        when(taskService.updateTask(anyLong(), any(Task.class))).thenThrow(new RuntimeException("Task not found with id: 999"));

        mockMvc.perform(put("/api/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_ShouldReturn200() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_WithNonExistentId_ShouldStillReturn200() throws Exception {
        doNothing().when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isOk());
    }
}