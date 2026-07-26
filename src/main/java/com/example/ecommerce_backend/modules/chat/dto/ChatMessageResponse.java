package com.example.ecommerce_backend.modules.chat.dto;

import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "Response containing chat message details")
public class ChatMessageResponse {
    @Schema(description = "Unique identifier of the message", example = "msg-uuid-123")
    private String uuid;
    @Schema(description = "ID of the room this message belongs to", example = "1")
    private Long roomId;
    @Schema(description = "Type of sender (USER or AGENT)", example = "USER")
    private String senderType;
    @Schema(description = "ID of the sender", example = "1")
    private Long senderId;
    @Schema(description = "Content of the message", example = "Hello, I need help with my order")
    private String content;
    @Schema(description = "Type of message (TEXT, TYPING, etc.)", example = "TEXT")
    private String messageType;
    @Schema(description = "Additional metadata in JSON format")
    private String metadata;
    @Schema(description = "Timestamp when the message was created", example = "2024-01-01T00:00:00Z")
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
