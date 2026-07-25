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
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "agent_id")
    private Long agentId;

    @Column(nullable = false)
    @Builder.Default
    private String status = "BOT_ACTIVE";

    private String topic;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant assignedAt;

    private Instant closedAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }
}
