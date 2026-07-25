package com.example.ecommerce_backend.modules.order.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusResponse;
import com.example.ecommerce_backend.modules.order.service.OrderService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private User testUser;
    private OrderResponse orderResponse;

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

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("pass")
                .isActive(true)
                .build();

        orderResponse = OrderResponse.builder()
                .id(1L)
                .uuid("order-uuid-1")
                .orderNumber("ORD-ABC123")
                .status(OrderStatusResponse.builder().id(1L).code("PENDING").name("Pending").sortOrder(1).isActive(true).build())
                .subtotal(BigDecimal.valueOf(100))
                .discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(100))
                .currency("USD")
                .items(List.of())
                .statusHistory(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createOrder_shouldReturnCreated() throws Exception {
        when(orderService.createOrder(eq(1L), any())).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders/checkout")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shippingAddressId\": 1, \"couponCode\": \"SAVE10\", \"notes\": \"Leave at door\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.response.uuid").value("order-uuid-1"));
    }

    @Test
    void getUserOrders_withPagination_shouldReturnPaginatedResponse() throws Exception {
        Page<OrderResponse> page = new PageImpl<>(List.of(orderResponse));
        when(orderService.getUserOrders(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/orders?page=0&size=20")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("order-uuid-1"))
                .andExpect(jsonPath("$.pagination").exists())
                .andExpect(jsonPath("$.message").value("Orders retrieved successfully"));
    }

    @Test
    void getUserOrders_withoutPagination_shouldReturnList() throws Exception {
        when(orderService.getUserOrders(eq(1L))).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/api/orders")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("order-uuid-1"))
                .andExpect(jsonPath("$.pagination").doesNotExist());
    }

    @Test
    void getOrderByUuid_shouldReturnOrder() throws Exception {
        when(orderService.getOrderByUuid("order-uuid-1", 1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/order-uuid-1")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("order-uuid-1"))
                .andExpect(jsonPath("$.message").value("Order retrieved successfully"));
    }

    @Test
    void cancelOrder_shouldReturnCancelledOrder() throws Exception {
        when(orderService.cancelOrder("order-uuid-1", 1L)).thenReturn(orderResponse);

        mockMvc.perform(patch("/api/orders/order-uuid-1/cancel")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("order-uuid-1"))
                .andExpect(jsonPath("$.message").value("Order cancelled successfully"));
    }

    @Test
    void updateOrderStatus_shouldReturnUpdatedOrder() throws Exception {
        OrderResponse updated = OrderResponse.builder()
                .id(1L)
                .uuid("order-uuid-1")
                .orderNumber("ORD-ABC123")
                .status(OrderStatusResponse.builder().id(2L).code("CONFIRMED").name("Confirmed").sortOrder(2).isActive(true).build())
                .subtotal(BigDecimal.valueOf(100))
                .discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(100))
                .currency("USD")
                .items(List.of())
                .statusHistory(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(orderService.updateOrderStatus("order-uuid-1", "CONFIRMED", "Payment received")).thenReturn(updated);

        mockMvc.perform(put("/api/orders/order-uuid-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CONFIRMED\", \"reason\": \"Payment received\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.status.code").value("CONFIRMED"))
                .andExpect(jsonPath("$.message").value("Order status updated successfully"));
    }
}
