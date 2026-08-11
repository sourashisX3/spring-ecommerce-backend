package com.example.ecommerce_backend.modules.chat.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.chat.dto.ChatMessageResponse;
import com.example.ecommerce_backend.modules.chat.dto.ChatRoomResponse;
import com.example.ecommerce_backend.modules.chat.dto.CreateRoomRequest;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.chat.service.ChatService;
import com.example.ecommerce_backend.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat", description = "Chat API")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Create a chat room", description = "Creates a new chat room for the authenticated user")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal User user) {
        ChatRoom room = chatService.createRoom(user.getId(), request.topic());
        return ApiResponse.created(ChatRoomResponse.from(room), "Chat room created");
    }

    @Operation(summary = "Get user chat rooms", description = "Retrieves all chat rooms for the authenticated user with pagination")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<Page<ChatRoomResponse>>> getUserRooms(
            @AuthenticationPrincipal User user, @ParameterObject Pageable pageable) {
        Page<ChatRoomResponse> rooms = chatService.getUserRooms(user.getId(), pageable)
                .map(ChatRoomResponse::from);
        return ApiResponse.success(rooms, "Chat rooms retrieved");
    }

    @Operation(summary = "Get open chat rooms", description = "Retrieves all open (unassigned) chat rooms with pagination")
    @GetMapping("/rooms/open")
    public ResponseEntity<ApiResponse<Page<ChatRoomResponse>>> getOpenRooms(@ParameterObject Pageable pageable) {
        Page<ChatRoomResponse> rooms = chatService.getOpenRooms(pageable)
                .map(ChatRoomResponse::from);
        return ApiResponse.success(rooms, "Open chat rooms retrieved");
    }

    @Operation(summary = "Get all chat rooms (admin)", description = "Retrieves all chat rooms with pagination")
    @GetMapping("/rooms/all")
    public ResponseEntity<ApiResponse<Page<ChatRoomResponse>>> listAllRooms(@ParameterObject Pageable pageable) {
        return ApiResponse.success(chatService.listAllRoomResponses(pageable), "Chat rooms retrieved");
    }

    @Operation(summary = "Get room messages", description = "Retrieves all messages for a specific chat room with pagination")
    @GetMapping("/rooms/{uuid}/messages")
    public ResponseEntity<ApiResponse<Page<ChatMessageResponse>>> getRoomMessages(
            @PathVariable String uuid, @ParameterObject Pageable pageable) {
        ChatRoom room = chatService.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + uuid));
        Page<ChatMessageResponse> messages = chatService.getRoomMessages(room.getId(), pageable)
                .map(ChatMessageResponse::from);
        return ApiResponse.success(messages, "Messages retrieved");
    }

    @Operation(summary = "Assign room to agent", description = "Assigns a chat room to the authenticated agent")
    @PatchMapping("/rooms/{uuid}/assign")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> assignRoom(
            @PathVariable String uuid, @AuthenticationPrincipal User user) {
        ChatRoom room = chatService.assignAgent(uuid, user.getId());
        return ApiResponse.success(ChatRoomResponse.from(room), "Room assigned");
    }

    @Operation(summary = "Close a chat room", description = "Closes a chat room by its UUID")
    @PatchMapping("/rooms/{uuid}/close")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> closeRoom(@PathVariable String uuid) {
        ChatRoom room = chatService.closeRoom(uuid);
        return ApiResponse.success(ChatRoomResponse.from(room), "Room closed");
    }
}
