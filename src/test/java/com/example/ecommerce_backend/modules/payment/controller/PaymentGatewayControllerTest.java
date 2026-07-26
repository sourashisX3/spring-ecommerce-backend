package com.example.ecommerce_backend.modules.payment.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentGatewayResponse;
import com.example.ecommerce_backend.modules.payment.service.PaymentGatewayService;
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
class PaymentGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private PaymentGatewayResponse gatewayResponse;

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

        gatewayResponse = PaymentGatewayResponse.builder()
                .id(1L)
                .uuid("gateway-uuid-1")
                .code("STRIPE")
                .name("Stripe")
                .description("Stripe payment gateway")
                .configTemplate("{\"apiKey\": \"...\"}")
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void getAll_shouldReturnGateways() throws Exception {
        when(paymentGatewayService.getAll()).thenReturn(List.of(gatewayResponse));

        mockMvc.perform(get("/payment-gateways"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].code").value("STRIPE"))
                .andExpect(jsonPath("$.message").value("Payment gateways retrieved successfully"));
    }

    @Test
    void getByUuid_shouldReturnGateway() throws Exception {
        when(paymentGatewayService.getByUuid("gateway-uuid-1")).thenReturn(gatewayResponse);

        mockMvc.perform(get("/payment-gateways/gateway-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("STRIPE"))
                .andExpect(jsonPath("$.message").value("Payment gateway retrieved successfully"));
    }

    @Test
    void getByCode_shouldReturnGateway() throws Exception {
        when(paymentGatewayService.getByCode("STRIPE")).thenReturn(gatewayResponse);

        mockMvc.perform(get("/payment-gateways/code/STRIPE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("STRIPE"))
                .andExpect(jsonPath("$.message").value("Payment gateway retrieved successfully"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        when(paymentGatewayService.create(any())).thenReturn(gatewayResponse);

        mockMvc.perform(post("/payment-gateways")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"STRIPE\", \"name\": \"Stripe\", \"description\": \"Stripe payment gateway\", \"configTemplate\": \"{}\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Payment gateway created successfully"))
                .andExpect(jsonPath("$.response.code").value("STRIPE"));
    }

    @Test
    void update_shouldReturnUpdatedGateway() throws Exception {
        when(paymentGatewayService.update(eq("gateway-uuid-1"), any())).thenReturn(gatewayResponse);

        mockMvc.perform(put("/payment-gateways/gateway-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"STRIPE\", \"name\": \"Stripe Updated\", \"description\": \"Updated description\", \"configTemplate\": \"{}\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment gateway updated successfully"))
                .andExpect(jsonPath("$.response.code").value("STRIPE"));
    }

    @Test
    void toggleStatus_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(patch("/payment-gateways/gateway-uuid-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment gateway status updated successfully"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/payment-gateways/gateway-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment gateway deleted successfully"));
    }
}
