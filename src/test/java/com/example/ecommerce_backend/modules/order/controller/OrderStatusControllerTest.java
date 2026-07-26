package com.example.ecommerce_backend.modules.order.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusResponse;
import com.example.ecommerce_backend.modules.order.service.OrderStatusService;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderStatusService orderStatusService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private OrderStatusResponse statusResponse;

    @BeforeEach
    void setUp() throws Throwable {
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

        statusResponse = OrderStatusResponse.builder()
                .id(1L)
                .uuid("status-uuid-1")
                .code("PENDING")
                .name("Pending")
                .description("Order is pending")
                .sortOrder(1)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void getAll_shouldReturnStatuses() throws Exception {
        when(orderStatusService.getAll()).thenReturn(List.of(statusResponse));

        mockMvc.perform(get("/order-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].code").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Order statuses retrieved successfully"));
    }

    @Test
    void getByUuid_shouldReturnStatus() throws Exception {
        when(orderStatusService.getByUuid("status-uuid-1")).thenReturn(statusResponse);

        mockMvc.perform(get("/order-statuses/status-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Order status retrieved successfully"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        when(orderStatusService.create(any())).thenReturn(statusResponse);

        mockMvc.perform(post("/order-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"PENDING\", \"name\": \"Pending\", \"description\": \"Order is pending\", \"sortOrder\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Order status created successfully"))
                .andExpect(jsonPath("$.response.code").value("PENDING"));
    }

    @Test
    void update_shouldReturnUpdatedStatus() throws Exception {
        when(orderStatusService.update(eq("status-uuid-1"), any())).thenReturn(statusResponse);

        mockMvc.perform(put("/order-statuses/status-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"PENDING\", \"name\": \"Pending\", \"description\": \"Updated description\", \"sortOrder\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status updated successfully"))
                .andExpect(jsonPath("$.response.code").value("PENDING"));
    }

    @Test
    void toggleStatus_shouldReturnUpdatedStatus() throws Exception {
        when(orderStatusService.toggleStatus("status-uuid-1", false)).thenReturn(
                OrderStatusResponse.builder()
                        .id(1L).uuid("status-uuid-1").code("PENDING").name("Pending")
                        .sortOrder(1).isActive(false).build()
        );

        mockMvc.perform(patch("/order-statuses/status-uuid-1/status?active=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status updated successfully"))
                .andExpect(jsonPath("$.response.isActive").value(false));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/order-statuses/status-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status deleted successfully"));
    }
}
