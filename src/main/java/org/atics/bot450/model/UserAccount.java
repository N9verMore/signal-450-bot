package org.atics.bot450.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // логинимся по телефону (или любому логину)
    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 200)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (role == null) role = "ROLE_USER";
    }
}
