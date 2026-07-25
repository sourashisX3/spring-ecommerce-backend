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
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(nullable = false)
    @Builder.Default
    private String senderType = "USER";

    @Column(name = "sender_id")
    private Long senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private String messageType = "TEXT";

    @Column(columnDefinition = "JSON")
    private String metadata;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant readAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }
}
