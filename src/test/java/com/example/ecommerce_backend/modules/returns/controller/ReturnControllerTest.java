package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnResponse;
import com.example.ecommerce_backend.modules.returns.service.ReturnService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReturnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReturnService returnService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private User testUser;

    @BeforeEach
    void setUp() throws Throwable {
        testUser = User.builder()
                .id(1L).uuid("user-uuid")
                .email("test@test.com")
                .firstName("Test").lastName("User")
                .build();

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                try {
                    return ((org.aspectj.lang.ProceedingJoinPoint) invocation.getArgument(0)).proceed();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }).when(authorizationAspect).checkPermission(any(), any());
    }

    @Test
    void getAll_withoutPagination_shouldReturnList() throws Exception {
        ReturnResponse ret = ReturnResponse.builder()
                .uuid("return-uuid").status("PENDING")
                .build();

        when(returnService.getAll()).thenReturn(List.of(ret));

        mockMvc.perform(get("/api/returns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("return-uuid"));
    }

    @Test
    void getAll_withPagination_shouldReturnPaginated() throws Exception {
        ReturnResponse ret = ReturnResponse.builder()
                .uuid("return-uuid").status("PENDING")
                .build();

        Page<ReturnResponse> page = new PageImpl<>(List.of(ret));
        when(returnService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/returns?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("return-uuid"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void getByUuid_shouldReturnReturn() throws Exception {
        ReturnResponse ret = ReturnResponse.builder()
                .uuid("return-uuid").status("PENDING")
                .build();

        when(returnService.getByUuid("return-uuid")).thenReturn(ret);

        mockMvc.perform(get("/api/returns/return-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("return-uuid"));
    }

    @Test
    void getMyReturns_withoutPagination_shouldReturnList() throws Exception {
        ReturnResponse ret = ReturnResponse.builder()
                .uuid("return-uuid").status("PENDING")
                .build();

        when(returnService.getByUserId(1L)).thenReturn(List.of(ret));

        mockMvc.perform(get("/api/returns/my")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("return-uuid"));
    }

    @Test
    void getMyReturns_withPagination_shouldReturnPaginated() throws Exception {
        ReturnResponse ret = ReturnResponse.builder()
                .uuid("return-uuid").status("PENDING")
                .build();

        Page<ReturnResponse> page = new PageImpl<>(List.of(ret));
        when(returnService.getByUserId(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/returns/my?page=0&size=10")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("return-uuid"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        ReturnResponse response = ReturnResponse.builder()
                .uuid("return-uuid").reason("Defective product")
                .build();

        when(returnService.create(any(User.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/returns")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderUuid\":\"order-uuid\",\"reason\":\"Defective product\",\"items\":[{\"orderItemId\":1,\"quantity\":1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.uuid").value("return-uuid"));
    }

    @Test
    void updateStatus_shouldReturnUpdated() throws Exception {
        ReturnResponse response = ReturnResponse.builder()
                .uuid("return-uuid").status("APPROVED")
                .resolutionNotes("Approved after review")
                .build();

        when(returnService.updateStatus(eq("return-uuid"), eq("APPROVED"), eq("Approved after review")))
                .thenReturn(response);

        mockMvc.perform(patch("/api/returns/return-uuid/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\",\"resolutionNotes\":\"Approved after review\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.status").value("APPROVED"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        doNothing().when(returnService).delete("return-uuid");

        mockMvc.perform(delete("/api/returns/return-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Return request deleted successfully"));
    }
}
