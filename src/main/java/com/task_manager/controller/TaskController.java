package com.task_manager.controller;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task_manager.dto.TaskDTO;
import com.task_manager.mapper.TaskMapper;
import com.task_manager.model.Task;
import com.task_manager.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks().stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(TaskMapper::toDTO)
                .map(taskDTO -> ResponseEntity.ok().body(taskDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task task = TaskMapper.toEntity(taskDTO);
        Task createdTask = taskService.createTask(task);
        return TaskMapper.toDTO(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        try {
            Task taskDetails = TaskMapper.toEntity(taskDTO);
            Task updatedTask = taskService.updateTask(id, taskDetails);
            return ResponseEntity.ok(TaskMapper.toDTO(updatedTask));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
