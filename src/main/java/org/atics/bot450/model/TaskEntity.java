package org.atics.bot450.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Логин пользователя (= его номер телефона, с которого отправляем) */
    @Column(nullable = false)
    private String ownerUsername;

    @Column(nullable = false)
    private String groupId;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false, length = 4000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = TaskStatus.PENDING;
    }
}
