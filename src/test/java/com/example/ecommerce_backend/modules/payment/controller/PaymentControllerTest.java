package com.example.ecommerce_backend.modules.payment.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentGatewayResponse;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentResponse;
import com.example.ecommerce_backend.modules.payment.dto.response.RefundResponse;
import com.example.ecommerce_backend.modules.payment.service.PaymentService;
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
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private User testUser;
    private PaymentResponse paymentResponse;

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

        paymentResponse = PaymentResponse.builder()
                .id(1L)
                .uuid("payment-uuid-1")
                .orderId(100L)
                .userId(1L)
                .gateway(PaymentGatewayResponse.builder().id(1L).code("STRIPE").name("Stripe").isActive(true).build())
                .amount(BigDecimal.valueOf(99.99))
                .currency("USD")
                .status("COMPLETED")
                .method("credit_card")
                .gatewayTransactionId("TXN-123456")
                .paidAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void processPayment_shouldReturnCreated() throws Exception {
        when(paymentService.processPayment(any(), eq(1L))).thenReturn(paymentResponse);

        mockMvc.perform(post("/payments/pay")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\": 100, \"gatewayCode\": \"STRIPE\", \"amount\": 99.99, \"currency\": \"USD\", \"method\": \"credit_card\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Payment processed successfully"))
                .andExpect(jsonPath("$.response.uuid").value("payment-uuid-1"));
    }

    @Test
    void getByUuid_shouldReturnPayment() throws Exception {
        when(paymentService.getByUuid("payment-uuid-1")).thenReturn(paymentResponse);

        mockMvc.perform(get("/payments/payment-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("payment-uuid-1"))
                .andExpect(jsonPath("$.message").value("Payment retrieved successfully"));
    }

    @Test
    void getByOrderId_shouldReturnPayment() throws Exception {
        when(paymentService.getByOrderId(100L)).thenReturn(paymentResponse);

        mockMvc.perform(get("/payments/order/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("payment-uuid-1"))
                .andExpect(jsonPath("$.message").value("Payment retrieved successfully"));
    }

    @Test
    void getUserPayments_withPagination_shouldReturnPaginatedResponse() throws Exception {
        Page<PaymentResponse> page = new PageImpl<>(List.of(paymentResponse));
        when(paymentService.getUserPayments(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/payments?page=0&size=20")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("payment-uuid-1"))
                .andExpect(jsonPath("$.pagination").exists())
                .andExpect(jsonPath("$.message").value("Payments retrieved successfully"));
    }

    @Test
    void getUserPayments_withoutPagination_shouldReturnList() throws Exception {
        when(paymentService.getUserPayments(eq(1L))).thenReturn(List.of(paymentResponse));

        mockMvc.perform(get("/payments")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("payment-uuid-1"))
                .andExpect(jsonPath("$.pagination").doesNotExist());
    }

    @Test
    void getRefunds_shouldReturnRefundList() throws Exception {
        RefundResponse refund = RefundResponse.builder()
                .id(1L).uuid("refund-uuid-1").paymentId(1L).amount(BigDecimal.TEN)
                .reason("Damaged item").status("COMPLETED").build();

        when(paymentService.getRefundsByPaymentId(1L)).thenReturn(List.of(refund));

        mockMvc.perform(get("/payments/1/refunds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("refund-uuid-1"))
                .andExpect(jsonPath("$.message").value("Refunds retrieved successfully"));
    }

    @Test
    void processRefund_shouldReturnCreated() throws Exception {
        RefundResponse refund = RefundResponse.builder()
                .id(1L).uuid("refund-uuid-1").paymentId(1L).amount(BigDecimal.TEN)
                .reason("Damaged item").status("COMPLETED").gatewayRefundId("RFD-123456").build();

        when(paymentService.processRefund(any())).thenReturn(refund);

        mockMvc.perform(post("/payments/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentId\": 1, \"amount\": 10.00, \"reason\": \"Damaged item\", \"returnRequestId\": 5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Refund processed successfully"))
                .andExpect(jsonPath("$.response.uuid").value("refund-uuid-1"));
    }
}
