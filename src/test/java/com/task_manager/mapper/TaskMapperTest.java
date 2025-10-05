package com.task_manager.mapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.task_manager.dto.TaskDTO;
import com.task_manager.model.Status;
import com.task_manager.model.Task;

public class TaskMapperTest {

    private Task task;
    private TaskDTO taskDTO;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.now().plusDays(1);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.TODO);
        task.setDueDate(testDate);

        taskDTO = new TaskDTO(1L, "Test Task", "Test Description", Status.TODO, testDate);
    }

    @Test
    void toDTO_WithValidTask_ShouldReturnTaskDTO() {
        TaskDTO result = TaskMapper.toDTO(task);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStatus()).isEqualTo(Status.TODO);
        assertThat(result.getDueDate()).isEqualTo(testDate);
    }

    @Test
    void toDTO_WithNullTask_ShouldReturnNull() {
        TaskDTO result = TaskMapper.toDTO(null);

        assertThat(result).isNull();
    }

    @Test
    void toDTO_WithTaskHavingNullFields_ShouldReturnTaskDTOWithNullFields() {
        Task taskWithNulls = new Task();
        taskWithNulls.setId(2L);
        taskWithNulls.setTitle("Title Only");

        TaskDTO result = TaskMapper.toDTO(taskWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("Title Only");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getStatus()).isNull();
        assertThat(result.getDueDate()).isNull();
    }

    @Test
    void toDTO_WithAllStatusTypes_ShouldMapCorrectly() {
        task.setStatus(Status.TODO);
        assertThat(TaskMapper.toDTO(task).getStatus()).isEqualTo(Status.TODO);

        task.setStatus(Status.IN_PROGRESS);
        assertThat(TaskMapper.toDTO(task).getStatus()).isEqualTo(Status.IN_PROGRESS);

        task.setStatus(Status.DONE);
        assertThat(TaskMapper.toDTO(task).getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void toEntity_WithValidTaskDTO_ShouldReturnTask() {
        Task result = TaskMapper.toEntity(taskDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStatus()).isEqualTo(Status.TODO);
        assertThat(result.getDueDate()).isEqualTo(testDate);
    }

    @Test
    void toEntity_WithNullTaskDTO_ShouldReturnNull() {
        Task result = TaskMapper.toEntity(null);

        assertThat(result).isNull();
    }

    @Test
    void toEntity_WithTaskDTOHavingNullFields_ShouldReturnTaskWithNullFields() {
        TaskDTO taskDTOWithNulls = new TaskDTO(3L, "Title Only", null, null, null);

        Task result = TaskMapper.toEntity(taskDTOWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getTitle()).isEqualTo("Title Only");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getStatus()).isNull();
        assertThat(result.getDueDate()).isNull();
    }

    @Test
    void toEntity_WithAllStatusTypes_ShouldMapCorrectly() {
        taskDTO = new TaskDTO(1L, "Test Task", "Test Description", Status.TODO, testDate);
        assertThat(TaskMapper.toEntity(taskDTO).getStatus()).isEqualTo(Status.TODO);

        taskDTO = new TaskDTO(1L, "Test Task", "Test Description", Status.IN_PROGRESS, testDate);
        assertThat(TaskMapper.toEntity(taskDTO).getStatus()).isEqualTo(Status.IN_PROGRESS);

        taskDTO = new TaskDTO(1L, "Test Task", "Test Description", Status.DONE, testDate);
        assertThat(TaskMapper.toEntity(taskDTO).getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void updateEntityFromDTO_WithValidInputs_ShouldUpdateTask() {
        Task targetTask = new Task();
        targetTask.setId(5L);
        targetTask.setTitle("Old Title");
        targetTask.setDescription("Old Description");
        targetTask.setStatus(Status.DONE);
        targetTask.setDueDate(LocalDate.now().minusDays(1));

        TaskDTO sourceDTO = new TaskDTO(null, "New Title", "New Description", Status.IN_PROGRESS, testDate);

        TaskMapper.updateEntityFromDTO(targetTask, sourceDTO);

        assertThat(targetTask.getId()).isEqualTo(5L); // ID should not change
        assertThat(targetTask.getTitle()).isEqualTo("New Title");
        assertThat(targetTask.getDescription()).isEqualTo("New Description");
        assertThat(targetTask.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(targetTask.getDueDate()).isEqualTo(testDate);
    }

    @Test
    void updateEntityFromDTO_WithNullTask_ShouldNotThrowException() {
        TaskDTO sourceDTO = new TaskDTO(1L, "Title", "Description", Status.TODO, testDate);

        // Should not throw any exception
        TaskMapper.updateEntityFromDTO(null, sourceDTO);
    }

    @Test
    void updateEntityFromDTO_WithNullTaskDTO_ShouldNotUpdateTask() {
        Task originalTask = new Task();
        originalTask.setId(1L);
        originalTask.setTitle("Original Title");
        originalTask.setDescription("Original Description");
        originalTask.setStatus(Status.TODO);
        originalTask.setDueDate(testDate);

        TaskMapper.updateEntityFromDTO(originalTask, null);

        // Task should remain unchanged
        assertThat(originalTask.getId()).isEqualTo(1L);
        assertThat(originalTask.getTitle()).isEqualTo("Original Title");
        assertThat(originalTask.getDescription()).isEqualTo("Original Description");
        assertThat(originalTask.getStatus()).isEqualTo(Status.TODO);
        assertThat(originalTask.getDueDate()).isEqualTo(testDate);
    }

    @Test
    void updateEntityFromDTO_WithBothNull_ShouldNotThrowException() {
        // Should not throw any exception
        TaskMapper.updateEntityFromDTO(null, null);
    }

    @Test
    void updateEntityFromDTO_WithNullFieldsInDTO_ShouldSetFieldsToNull() {
        Task targetTask = new Task();
        targetTask.setId(1L);
        targetTask.setTitle("Original Title");
        targetTask.setDescription("Original Description");
        targetTask.setStatus(Status.TODO);
        targetTask.setDueDate(testDate);

        TaskDTO sourceDTO = new TaskDTO(2L, null, null, null, null);

        TaskMapper.updateEntityFromDTO(targetTask, sourceDTO);

        assertThat(targetTask.getId()).isEqualTo(1L); // ID should not change
        assertThat(targetTask.getTitle()).isNull();
        assertThat(targetTask.getDescription()).isNull();
        assertThat(targetTask.getStatus()).isNull();
        assertThat(targetTask.getDueDate()).isNull();
    }

    @Test
    void roundTripConversion_ShouldPreserveData() {
        // Convert Task to DTO and back to Task
        TaskDTO dto = TaskMapper.toDTO(task);
        Task convertedTask = TaskMapper.toEntity(dto);

        assertThat(convertedTask.getId()).isEqualTo(task.getId());
        assertThat(convertedTask.getTitle()).isEqualTo(task.getTitle());
        assertThat(convertedTask.getDescription()).isEqualTo(task.getDescription());
        assertThat(convertedTask.getStatus()).isEqualTo(task.getStatus());
        assertThat(convertedTask.getDueDate()).isEqualTo(task.getDueDate());
    }

    @Test
    void roundTripConversion_WithNullFields_ShouldPreserveNulls() {
        Task taskWithNulls = new Task();
        taskWithNulls.setId(1L);
        taskWithNulls.setTitle("Title");

        TaskDTO dto = TaskMapper.toDTO(taskWithNulls);
        Task convertedTask = TaskMapper.toEntity(dto);

        assertThat(convertedTask.getId()).isEqualTo(taskWithNulls.getId());
        assertThat(convertedTask.getTitle()).isEqualTo(taskWithNulls.getTitle());
        assertThat(convertedTask.getDescription()).isNull();
        assertThat(convertedTask.getStatus()).isNull();
        assertThat(convertedTask.getDueDate()).isNull();
    }
}