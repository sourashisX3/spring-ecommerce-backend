package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnTypeResponse;
import com.example.ecommerce_backend.modules.returns.service.ReturnTypeService;
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
class ReturnTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReturnTypeService returnTypeService;

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
        ReturnTypeResponse type = ReturnTypeResponse.builder()
                .uuid("type-uuid")
                .code("REFUND")
                .name("Refund")
                .build();

        when(returnTypeService.getAll()).thenReturn(List.of(type));

        mockMvc.perform(get("/return-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].code").value("REFUND"));
    }

    @Test
    void getByUuid_shouldReturnType() throws Exception {
        ReturnTypeResponse type = ReturnTypeResponse.builder()
                .uuid("type-uuid")
                .code("REFUND")
                .name("Refund")
                .build();

        when(returnTypeService.getByUuid("type-uuid")).thenReturn(type);

        mockMvc.perform(get("/return-types/type-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("REFUND"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        ReturnTypeResponse response = ReturnTypeResponse.builder()
                .uuid("type-uuid")
                .code("REFUND")
                .name("Refund")
                .build();

        when(returnTypeService.create(any())).thenReturn(response);

        mockMvc.perform(post("/return-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"REFUND\",\"name\":\"Refund\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.code").value("REFUND"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        ReturnTypeResponse response = ReturnTypeResponse.builder()
                .uuid("type-uuid")
                .code("REFUND")
                .name("Full Refund")
                .build();

        when(returnTypeService.update(eq("type-uuid"), any())).thenReturn(response);

        mockMvc.perform(put("/return-types/type-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"REFUND\",\"name\":\"Full Refund\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name").value("Full Refund"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        doNothing().when(returnTypeService).delete("type-uuid");

        mockMvc.perform(delete("/return-types/type-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Return type deleted successfully"));
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        when(returnTypeService.toggleStatus("type-uuid", false)).thenReturn(true);

        mockMvc.perform(patch("/return-types/type-uuid/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Return type status updated successfully"));
    }
}
