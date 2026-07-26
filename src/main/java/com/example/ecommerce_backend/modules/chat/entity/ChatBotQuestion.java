package com.example.ecommerce_backend.modules.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_bot_questions")
public class ChatBotQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(unique = true, nullable = false)
    private String questionKey;

    @Column(nullable = false)
    private String questionText;

    @Column(columnDefinition = "JSON")
    private String options;

    @Column(columnDefinition = "TEXT")
    private String botResponse;

    @Column(nullable = false)
    @Builder.Default
    private boolean isEscalationPoint = false;

    @Column(nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
