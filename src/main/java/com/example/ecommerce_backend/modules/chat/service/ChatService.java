package com.example.ecommerce_backend.modules.chat.service;

import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.chat.exception.ChatRoomNotFoundException;
import com.example.ecommerce_backend.modules.chat.repository.ChatMessageRepository;
import com.example.ecommerce_backend.modules.chat.repository.ChatRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional
    public ChatRoom createRoom(Long userId, String topic) {
        ChatRoom room = ChatRoom.builder()
                .userId(userId)
                .status("BOT_ACTIVE")
                .topic(topic)
                .build();
        return chatRoomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Optional<ChatRoom> findByUuid(String uuid) {
        return chatRoomRepository.findByUuid(uuid);
    }

    @Transactional(readOnly = true)
    public Page<ChatRoom> getUserRooms(Long userId, Pageable pageable) {
        return chatRoomRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ChatRoom> getOpenRooms(Pageable pageable) {
        return chatRoomRepository.findByStatusOrderByCreatedAtAsc("AWAITING_AGENT", pageable);
    }

    @Transactional(readOnly = true)
    public Page<ChatRoom> getAgentRooms(Long agentId, Pageable pageable) {
        return chatRoomRepository.findByAgentIdOrderByCreatedAtDesc(agentId, pageable);
    }

    @Transactional
    public ChatRoom assignAgent(String roomUuid, Long agentId) {
        ChatRoom room = chatRoomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new ChatRoomNotFoundException("Chat room not found: " + roomUuid));
        room.setAgentId(agentId);
        room.setStatus("ACTIVE");
        room.setAssignedAt(Instant.now());
        return chatRoomRepository.save(room);
    }

    @Transactional
    public ChatRoom escalateToAgent(String roomUuid) {
        ChatRoom room = chatRoomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new ChatRoomNotFoundException("Chat room not found: " + roomUuid));
        room.setStatus("AWAITING_AGENT");
        return chatRoomRepository.save(room);
    }

    @Transactional
    public ChatRoom closeRoom(String roomUuid) {
        ChatRoom room = chatRoomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new ChatRoomNotFoundException("Chat room not found: " + roomUuid));
        room.setStatus("CLOSED");
        room.setClosedAt(Instant.now());
        return chatRoomRepository.save(room);
    }

    @Transactional
    public ChatMessage sendMessage(Long roomId, String senderType, Long senderId,
                                    String content, String messageType, String metadata) {
        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .senderType(senderType)
                .senderId(senderId)
                .content(content)
                .messageType(messageType)
                .metadata(metadata)
                .build();
        return chatMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessage> getRoomMessages(Long roomId, Pageable pageable) {
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId, pageable);
    }
}
