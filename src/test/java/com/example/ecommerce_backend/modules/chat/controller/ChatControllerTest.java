package com.example.ecommerce_backend.modules.chat.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.chat.service.ChatService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private User testUser;

    @BeforeEach
    void setUpAuth() throws Throwable {
        testUser = User.builder().id(1L).email("test@test.com").build();
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ProceedingJoinPoint pjp = invocation.getArgument(0);
                try {
                    return pjp.proceed();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }).when(authorizationAspect).checkPermission(any(), any());
    }

    @Test
    void createRoom_shouldReturnCreated() throws Exception {
        ChatRoom room = ChatRoom.builder()
                .uuid("room-uuid").userId(1L).topic("Help")
                .status("BOT_ACTIVE").createdAt(Instant.now())
                .build();
        when(chatService.createRoom(1L, "Help")).thenReturn(room);

        mockMvc.perform(post("/chat/rooms")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"Help\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.uuid").value("room-uuid"))
                .andExpect(jsonPath("$.message").value("Chat room created"));
    }

    @Test
    void createRoom_withoutTopic_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/chat/rooms")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRooms_shouldReturnPage() throws Exception {
        ChatRoom room = ChatRoom.builder()
                .uuid("room-uuid").userId(1L).topic("Help")
                .status("BOT_ACTIVE").createdAt(Instant.now())
                .build();
        when(chatService.getUserRooms(eq(1L), any())).thenReturn(new PageImpl<>(List.of(room)));

        mockMvc.perform(get("/chat/rooms").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content[0].uuid").value("room-uuid"))
                .andExpect(jsonPath("$.response.content[0].topic").value("Help"));
    }

    @Test
    void getOpenRooms_shouldReturnPage() throws Exception {
        ChatRoom room = ChatRoom.builder()
                .uuid("open-uuid").topic("Open room")
                .status("AWAITING_AGENT").createdAt(Instant.now())
                .build();
        when(chatService.getOpenRooms(any())).thenReturn(new PageImpl<>(List.of(room)));

        mockMvc.perform(get("/chat/rooms/open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content[0].uuid").value("open-uuid"))
                .andExpect(jsonPath("$.response.content[0].status").value("AWAITING_AGENT"));
    }

    @Test
    void getRoomMessages_shouldReturnPage() throws Exception {
        ChatRoom room = ChatRoom.builder().id(1L).uuid("room-uuid").build();
        ChatMessage msg = ChatMessage.builder()
                .uuid("msg-uuid").roomId(1L).content("Hello")
                .senderType("USER").createdAt(Instant.now())
                .build();
        when(chatService.findByUuid("room-uuid")).thenReturn(Optional.of(room));
        when(chatService.getRoomMessages(eq(1L), any())).thenReturn(new PageImpl<>(List.of(msg)));

        mockMvc.perform(get("/chat/rooms/room-uuid/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content[0].uuid").value("msg-uuid"))
                .andExpect(jsonPath("$.response.content[0].content").value("Hello"));
    }

    @Test
    void getRoomMessages_whenRoomNotFound_shouldReturn500() throws Exception {
        when(chatService.findByUuid("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/chat/rooms/nonexistent/messages"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void assignRoom_shouldReturnSuccess() throws Exception {
        ChatRoom room = ChatRoom.builder()
                .uuid("room-uuid").userId(1L).agentId(2L)
                .status("ACTIVE").assignedAt(Instant.now())
                .build();
        when(chatService.assignAgent("room-uuid", 1L)).thenReturn(room);

        mockMvc.perform(patch("/chat/rooms/room-uuid/assign")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("room-uuid"))
                .andExpect(jsonPath("$.response.status").value("ACTIVE"))
                .andExpect(jsonPath("$.message").value("Room assigned"));
    }

    @Test
    void closeRoom_shouldReturnSuccess() throws Exception {
        ChatRoom room = ChatRoom.builder()
                .uuid("room-uuid").status("CLOSED").closedAt(Instant.now())
                .build();
        when(chatService.closeRoom("room-uuid")).thenReturn(room);

        mockMvc.perform(patch("/chat/rooms/room-uuid/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("room-uuid"))
                .andExpect(jsonPath("$.response.status").value("CLOSED"))
                .andExpect(jsonPath("$.message").value("Room closed"));
    }
}
