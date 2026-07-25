package com.example.ecommerce_backend.modules.chat.dto;

import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatRoomResponse {
    private String uuid;
    private Long userId;
    private Long agentId;
    private String status;
    private String topic;
    private Instant createdAt;
    private Instant assignedAt;
    private Instant closedAt;

    public static ChatRoomResponse from(ChatRoom room) {
        return ChatRoomResponse.builder()
                .uuid(room.getUuid())
                .userId(room.getUserId())
                .agentId(room.getAgentId())
                .status(room.getStatus())
                .topic(room.getTopic())
                .createdAt(room.getCreatedAt())
                .assignedAt(room.getAssignedAt())
                .closedAt(room.getClosedAt())
                .build();
    }
}
