package com.example.ecommerce_backend.modules.chat.dto;

import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "Response containing chat room details")
public class ChatRoomResponse {
    @Schema(description = "Unique identifier of the chat room", example = "room-uuid-123")
    private String uuid;
    @Schema(description = "ID of the user who created the room", example = "1")
    private Long userId;
    @Schema(description = "ID of the assigned agent", example = "2")
    private Long agentId;
    @Schema(description = "Status of the room (OPEN, ASSIGNED, CLOSED)", example = "OPEN")
    private String status;
    @Schema(description = "Topic of the chat room", example = "Order inquiry")
    private String topic;
    @Schema(description = "Timestamp when the room was created", example = "2024-01-01T00:00:00Z")
    private Instant createdAt;
    @Schema(description = "Timestamp when an agent was assigned", example = "2024-01-01T00:05:00Z")
    private Instant assignedAt;
    @Schema(description = "Timestamp when the room was closed", example = "2024-01-01T01:00:00Z")
    private Instant closedAt;
    @Schema(description = "Display name of the customer")
    private String customerName;
    @Schema(description = "Email of the customer")
    private String customerEmail;
    @Schema(description = "Profile picture URL of the customer")
    private String customerProfilePictureUrl;
    @Schema(description = "Display name of the assigned agent")
    private String agentName;
    @Schema(description = "Content of the last message in the room")
    private String lastMessageContent;
    @Schema(description = "Sender type of the last message (USER, AGENT or BOT)")
    private String lastMessageSenderType;
    @Schema(description = "Timestamp of the last message in the room")
    private Instant lastMessageAt;

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

    public static ChatRoomResponse from(ChatRoom room, User customer, User agent, ChatMessage lastMessage) {
        ChatRoomResponse response = from(room);
        if (customer != null) {
            response.setCustomerName((customer.getFirstName() + " " + customer.getLastName()).trim());
            response.setCustomerEmail(customer.getEmail());
            response.setCustomerProfilePictureUrl(customer.getProfilePictureUrl());
        }
        if (agent != null) {
            response.setAgentName((agent.getFirstName() + " " + agent.getLastName()).trim());
        }
        if (lastMessage != null) {
            response.setLastMessageContent(lastMessage.getContent());
            response.setLastMessageSenderType(lastMessage.getSenderType());
            response.setLastMessageAt(lastMessage.getCreatedAt());
        }
        return response;
    }
}
