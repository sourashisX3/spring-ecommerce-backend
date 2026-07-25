package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnConditionResponse;
import com.example.ecommerce_backend.modules.returns.service.ReturnConditionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReturnConditionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReturnConditionService returnConditionService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    @BeforeEach
    void setUp() throws Throwable {
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
    void getAll_shouldReturnList() throws Exception {
        ReturnConditionResponse condition = ReturnConditionResponse.builder()
                .uuid("condition-uuid")
                .code("DAMAGED")
                .name("Damaged")
                .build();

        when(returnConditionService.getAll()).thenReturn(List.of(condition));

        mockMvc.perform(get("/api/return-conditions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].code").value("DAMAGED"));
    }

    @Test
    void getByUuid_shouldReturnCondition() throws Exception {
        ReturnConditionResponse condition = ReturnConditionResponse.builder()
                .uuid("condition-uuid")
                .code("DAMAGED")
                .name("Damaged")
                .build();

        when(returnConditionService.getByUuid("condition-uuid")).thenReturn(condition);

        mockMvc.perform(get("/api/return-conditions/condition-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("DAMAGED"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        ReturnConditionResponse response = ReturnConditionResponse.builder()
                .uuid("condition-uuid")
                .code("DAMAGED")
                .name("Damaged")
                .build();

        when(returnConditionService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/return-conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"DAMAGED\",\"name\":\"Damaged\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.code").value("DAMAGED"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        ReturnConditionResponse response = ReturnConditionResponse.builder()
                .uuid("condition-uuid")
                .code("DAMAGED")
                .name("Damaged Item")
                .build();

        when(returnConditionService.update(eq("condition-uuid"), any())).thenReturn(response);

        mockMvc.perform(put("/api/return-conditions/condition-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"DAMAGED\",\"name\":\"Damaged Item\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name").value("Damaged Item"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        doNothing().when(returnConditionService).delete("condition-uuid");

        mockMvc.perform(delete("/api/return-conditions/condition-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Return condition deleted successfully"));
    }
}
