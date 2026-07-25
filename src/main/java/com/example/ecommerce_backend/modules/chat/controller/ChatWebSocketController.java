package com.example.ecommerce_backend.modules.chat.controller;

import com.example.ecommerce_backend.modules.chat.dto.ChatMessageRequest;
import com.example.ecommerce_backend.modules.chat.dto.ChatMessageResponse;
import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.chat.service.ChatBotService;
import com.example.ecommerce_backend.modules.chat.service.ChatService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@Profile("!test")
public class ChatWebSocketController {

    private final ChatService chatService;
    private final ChatBotService chatBotService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService,
                                    ChatBotService chatBotService,
                                    SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.chatBotService = chatBotService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/rooms/{roomUuid}/send")
    public void sendMessage(@DestinationVariable String roomUuid,
                            @Payload ChatMessageRequest request,
                            SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null) return;

        User user = (User) ((org.springframework.security.core.Authentication) principal).getPrincipal();
        ChatRoom room = chatService.findByUuid(roomUuid)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String senderType = user.getId().equals(room.getUserId()) ? "USER" : "AGENT";

        ChatMessage saved = chatService.sendMessage(
                room.getId(), senderType, user.getId(),
                request.content(), "TEXT", null
        );

        ChatMessageResponse response = ChatMessageResponse.from(saved);
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomUuid, response);
    }

    @MessageMapping("/chat/rooms/{roomUuid}/typing")
    public void typing(@DestinationVariable String roomUuid,
                       SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null) return;

        User user = (User) ((org.springframework.security.core.Authentication) principal).getPrincipal();

        ChatRoom room = chatService.findByUuid(roomUuid)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String senderType = user.getId().equals(room.getUserId()) ? "USER" : "AGENT";

        ChatMessage typingMsg = chatService.sendMessage(
                room.getId(), senderType, user.getId(),
                "", "TYPING", null
        );

        messagingTemplate.convertAndSend("/topic/chat/room/" + roomUuid,
                ChatMessageResponse.from(typingMsg));
    }

    @MessageMapping("/chat/rooms/{roomUuid}/escalate")
    public void escalateToAgent(@DestinationVariable String roomUuid) {
        chatService.escalateToAgent(roomUuid);
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomUuid,
                (Object) Map.of("type", "AGENT_REQUESTED", "message", "Customer requested human agent"));
    }
}
