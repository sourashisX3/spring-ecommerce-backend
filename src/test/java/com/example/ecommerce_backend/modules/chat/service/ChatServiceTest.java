package com.example.ecommerce_backend.modules.chat.service;

import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.chat.repository.ChatMessageRepository;
import com.example.ecommerce_backend.modules.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void createRoom_shouldCreateAndReturnRoom() {
        ChatRoom expected = ChatRoom.builder()
                .id(1L).uuid("room-uuid").userId(1L)
                .status("BOT_ACTIVE").topic("Help")
                .createdAt(Instant.now()).build();
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(expected);

        ChatRoom result = chatService.createRoom(1L, "Help");

        assertThat(result.getUuid()).isEqualTo("room-uuid");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("BOT_ACTIVE");
        assertThat(result.getTopic()).isEqualTo("Help");
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    void findByUuid_shouldReturnRoom() {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("room-uuid").build();
        when(chatRoomRepository.findByUuid("room-uuid")).thenReturn(Optional.of(room));

        Optional<ChatRoom> result = chatService.findByUuid("room-uuid");

        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo("room-uuid");
    }

    @Test
    void findByUuid_whenNotFound_shouldReturnEmpty() {
        when(chatRoomRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        Optional<ChatRoom> result = chatService.findByUuid("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void getUserRooms_shouldReturnPage() {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("room-uuid").userId(1L).build();
        Page<ChatRoom> page = new PageImpl<>(List.of(room));
        when(chatRoomRepository.findByUserIdOrderByCreatedAtDesc(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        Page<ChatRoom> result = chatService.getUserRooms(1L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo("room-uuid");
    }

    @Test
    void getOpenRooms_shouldReturnPage() {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("open-uuid").status("AWAITING_AGENT").build();
        Page<ChatRoom> page = new PageImpl<>(List.of(room));
        when(chatRoomRepository.findByStatusOrderByCreatedAtAsc(eq("AWAITING_AGENT"), any(PageRequest.class)))
                .thenReturn(page);

        Page<ChatRoom> result = chatService.getOpenRooms(PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo("AWAITING_AGENT");
    }

    @Test
    void getAgentRooms_shouldReturnPage() {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("room-uuid").agentId(2L).build();
        Page<ChatRoom> page = new PageImpl<>(List.of(room));
        when(chatRoomRepository.findByAgentIdOrderByCreatedAtDesc(eq(2L), any(PageRequest.class)))
                .thenReturn(page);

        Page<ChatRoom> result = chatService.getAgentRooms(2L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void assignAgent_shouldAssignAndUpdateStatus() {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("room-uuid").status("BOT_ACTIVE").build();
        when(chatRoomRepository.findByUuid("room-uuid")).thenReturn(Optional.of(room));
        when(chatRoomRepository.save(room)).thenReturn(room);

        ChatRoom result = chatService.assignAgent("room-uuid", 2L);

        assertThat(result.getAgentId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getAssignedAt()).isNotNull();
    }

    @Test
    void assignAgent_whenRoomNotFound_shouldThrow() {
        when(chatRoomRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.assignAgent("nonexistent", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    void escalateToAgent_shouldUpdateStatus() {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("room-uuid").status("BOT_ACTIVE").build();
        when(chatRoomRepository.findByUuid("room-uuid")).thenReturn(Optional.of(room));
        when(chatRoomRepository.save(room)).thenReturn(room);

        ChatRoom result = chatService.escalateToAgent("room-uuid");

        assertThat(result.getStatus()).isEqualTo("AWAITING_AGENT");
    }

    @Test
    void escalateToAgent_whenRoomNotFound_shouldThrow() {
        when(chatRoomRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.escalateToAgent("nonexistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    void closeRoom_shouldCloseAndSetTimestamp() {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("room-uuid").status("ACTIVE").build();
        when(chatRoomRepository.findByUuid("room-uuid")).thenReturn(Optional.of(room));
        when(chatRoomRepository.save(room)).thenReturn(room);

        ChatRoom result = chatService.closeRoom("room-uuid");

        assertThat(result.getStatus()).isEqualTo("CLOSED");
        assertThat(result.getClosedAt()).isNotNull();
    }

    @Test
    void closeRoom_whenRoomNotFound_shouldThrow() {
        when(chatRoomRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.closeRoom("nonexistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    void sendMessage_shouldCreateAndReturnMessage() {
        ChatMessage expected = ChatMessage.builder()
                .id(1L).uuid("msg-uuid").roomId(1L)
                .senderType("USER").senderId(1L)
                .content("Hello").messageType("TEXT")
                .build();
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(expected);

        ChatMessage result = chatService.sendMessage(1L, "USER", 1L, "Hello", "TEXT", null);

        assertThat(result.getUuid()).isEqualTo("msg-uuid");
        assertThat(result.getContent()).isEqualTo("Hello");
        assertThat(result.getSenderType()).isEqualTo("USER");
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void getRoomMessages_shouldReturnPage() {
        ChatMessage msg = ChatMessage.builder().id(1L).uuid("msg-uuid").roomId(1L).content("Hello").build();
        Page<ChatMessage> page = new PageImpl<>(List.of(msg));
        when(chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        Page<ChatMessage> result = chatService.getRoomMessages(1L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo("msg-uuid");
    }
}
