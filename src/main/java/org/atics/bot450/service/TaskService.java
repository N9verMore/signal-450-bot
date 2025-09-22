package org.atics.bot450.service;

import lombok.RequiredArgsConstructor;
import org.atics.bot450.model.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final RestTemplate restTemplate;

    @Value("${org.atics.signal.api.url}")
    private String signalApiUrl;

    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Добавить задачу
     */
    public Task schedule(String groupName, String message, List<String> times, String createdBy) {
        Task task = new Task(idGenerator.getAndIncrement(), groupName, message, times, createdBy);
        tasks.add(task);
        return task;
    }

    /**
     * Получить все задачи
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Удалить задачу
     */
    public void deleteTask(Long id) {
        tasks.removeIf(t -> t.getId().equals(id));
    }

}
