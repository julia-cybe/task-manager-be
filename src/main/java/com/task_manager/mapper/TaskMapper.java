package com.task_manager.mapper;

import com.task_manager.dto.TaskDTO;
import com.task_manager.model.Task;

public class TaskMapper {

    public static TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }

        return new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getDueDate()
        );
    }

    public static Task toEntity(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        Task task = new Task();
        task.setId(taskDTO.getId());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());

        return task;
    }

    public static void updateEntityFromDTO(Task task, TaskDTO taskDTO) {
        if (task == null || taskDTO == null) {
            return;
        }

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
    }
}