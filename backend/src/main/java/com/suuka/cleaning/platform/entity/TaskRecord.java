package com.suuka.cleaning.platform.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import com.suuka.cleaning.platform.enums.PlatformModule;
import com.suuka.cleaning.platform.enums.TaskPriority;
import com.suuka.cleaning.platform.enums.TaskStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_records")
public class TaskRecord extends AuditableEntity {
    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;

    private String assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformModule relatedModule;

    private String relatedEntityId;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus = TaskStatus.OPEN;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public PlatformModule getRelatedModule() { return relatedModule; }
    public void setRelatedModule(PlatformModule relatedModule) { this.relatedModule = relatedModule; }
    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public TaskStatus getTaskStatus() { return taskStatus; }
    public void setTaskStatus(TaskStatus taskStatus) { this.taskStatus = taskStatus; }
}
