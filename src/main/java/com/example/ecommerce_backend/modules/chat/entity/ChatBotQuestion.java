package com.example.ecommerce_backend.modules.chat.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
