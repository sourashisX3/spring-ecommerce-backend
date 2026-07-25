package com.example.ecommerce_backend.modules.chat.dto;

import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatMessageResponse {
    private String uuid;
    private Long roomId;
    private String senderType;
    private Long senderId;
    private String content;
    private String messageType;
    private String metadata;
    private Instant createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return ChatMessageResponse.builder()
                .uuid(message.getUuid())
                .roomId(message.getRoomId())
                .senderType(message.getSenderType())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .metadata(message.getMetadata())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
