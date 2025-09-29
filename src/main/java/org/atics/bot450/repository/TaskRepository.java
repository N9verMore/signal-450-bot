package org.atics.bot450.repository;

import org.atics.bot450.model.TaskEntity;
import org.atics.bot450.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByOwnerUsernameOrderByScheduledAtDesc(String ownerUsername);

    List<TaskEntity> findTop100ByStatusAndScheduledAtBeforeOrderByScheduledAtAsc(TaskStatus status, LocalDateTime before);
}
