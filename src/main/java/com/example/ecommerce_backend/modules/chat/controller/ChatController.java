package com.example.ecommerce_backend.modules.chat.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.chat.dto.ChatMessageResponse;
import com.example.ecommerce_backend.modules.chat.dto.ChatRoomResponse;
import com.example.ecommerce_backend.modules.chat.dto.CreateRoomRequest;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.chat.service.ChatService;
import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal User user) {
        ChatRoom room = chatService.createRoom(user.getId(), request.topic());
        return ApiResponse.created(ChatRoomResponse.from(room), "Chat room created");
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<Page<ChatRoomResponse>>> getUserRooms(
            @AuthenticationPrincipal User user, Pageable pageable) {
        Page<ChatRoomResponse> rooms = chatService.getUserRooms(user.getId(), pageable)
                .map(ChatRoomResponse::from);
        return ApiResponse.success(rooms, "Chat rooms retrieved");
    }

    @GetMapping("/rooms/open")
    public ResponseEntity<ApiResponse<Page<ChatRoomResponse>>> getOpenRooms(Pageable pageable) {
        Page<ChatRoomResponse> rooms = chatService.getOpenRooms(pageable)
                .map(ChatRoomResponse::from);
        return ApiResponse.success(rooms, "Open chat rooms retrieved");
    }

    @GetMapping("/rooms/{uuid}/messages")
    public ResponseEntity<ApiResponse<Page<ChatMessageResponse>>> getRoomMessages(
            @PathVariable String uuid, Pageable pageable) {
        ChatRoom room = chatService.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + uuid));
        Page<ChatMessageResponse> messages = chatService.getRoomMessages(room.getId(), pageable)
                .map(ChatMessageResponse::from);
        return ApiResponse.success(messages, "Messages retrieved");
    }

    @PatchMapping("/rooms/{uuid}/assign")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> assignRoom(
            @PathVariable String uuid, @AuthenticationPrincipal User user) {
        ChatRoom room = chatService.assignAgent(uuid, user.getId());
        return ApiResponse.success(ChatRoomResponse.from(room), "Room assigned");
    }

    @PatchMapping("/rooms/{uuid}/close")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> closeRoom(@PathVariable String uuid) {
        ChatRoom room = chatService.closeRoom(uuid);
        return ApiResponse.success(ChatRoomResponse.from(room), "Room closed");
    }
}
