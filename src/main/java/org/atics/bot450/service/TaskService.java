package org.atics.bot450.service;

import lombok.RequiredArgsConstructor;
import org.atics.bot450.model.Task;
import org.atics.bot450.model.TaskEntity;
import org.atics.bot450.repository.TaskRepository;
import org.atics.bot450.model.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repo;
    private final SignalService signal;

    /** Создаём сразу несколько задач — по каждому выбранному времени. */
    @Transactional
    public List<Task> schedule(String groupIdOrName,
                               boolean isGroupId,
                               String message,
                               List<String> timesIso,
                               String ownerUsername) {

        String groupId;
        String groupName;

        if (isGroupId) {
            groupId = groupIdOrName;
            groupName = signal.listGroupsFor(ownerUsername).stream()
                    .filter(g -> Objects.equals(g.getId(), groupId))
                    .map(g -> Optional.ofNullable(g.getName()).orElse(g.getId()))
                    .findFirst()
                    .orElse(groupId);
        } else {
            groupName = groupIdOrName;
            groupId = signal.resolveGroupIdByName(ownerUsername, groupName)
                    .orElseThrow(() -> new IllegalArgumentException("Group with name '" + groupName + "' not found"));
        }

        List<TaskEntity> entities = new ArrayList<>();
        for (String iso : timesIso) {
            LocalDateTime dt;
            try {
                dt = LocalDateTime.parse(iso);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid datetime format: " + iso + " (expected HTML5 datetime-local ISO)");
            }
            entities.add(TaskEntity.builder()
                    .ownerUsername(ownerUsername)
                    .groupId(groupId)
                    .groupName(groupName)
                    .message(message)
                    .scheduledAt(dt)
                    .status(TaskStatus.PENDING)
                    .build());
        }

        List<TaskEntity> saved = repo.saveAll(entities);
        return saved.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Task> getUserTasks(String ownerUsername) {
        return repo.findByOwnerUsernameOrderByScheduledAtDesc(ownerUsername)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteTask(Long id, String ownerUsername) {
        TaskEntity e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Task not found"));
        if (!Objects.equals(e.getOwnerUsername(), ownerUsername)) {
            throw new SecurityException("Cannot delete task of another user");
        }
        repo.delete(e);
    }

    @Transactional
    public Task updateTask(Long id, String ownerUsername, String newMessage, String newDatetimeIso) {
        TaskEntity e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Task not found"));
        if (!Objects.equals(e.getOwnerUsername(), ownerUsername)) {
            throw new SecurityException("Cannot update task of another user");
        }
        if (newMessage != null && !newMessage.isBlank()) {
            e.setMessage(newMessage);
        }
        if (newDatetimeIso != null && !newDatetimeIso.isBlank()) {
            e.setScheduledAt(LocalDateTime.parse(newDatetimeIso));
        }
        e.setStatus(TaskStatus.PENDING); // если редактировали — вернём в ожидание
        return toDto(repo.save(e));
    }

    private Task toDto(TaskEntity e) {
        return new Task(
                e.getId(),
                e.getGroupName(),
                e.getMessage(),
                List.of(e.getScheduledAt().toString()),
                e.getOwnerUsername()
        );
    }
}
