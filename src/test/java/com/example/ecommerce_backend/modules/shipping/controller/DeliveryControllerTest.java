package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.shipping.dto.response.DeliveryResponse;
import com.example.ecommerce_backend.modules.shipping.service.DeliveryService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService deliveryService;

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
    void getByOrderId_shouldReturnList() throws Exception {
        DeliveryResponse delivery = DeliveryResponse.builder()
                .uuid("delivery-uuid").orderId(1L)
                .trackingNumber("TRACK123").status("PENDING")
                .build();

        when(deliveryService.getByOrderId(1L)).thenReturn(List.of(delivery));

        mockMvc.perform(get("/deliveries/order/1")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("delivery-uuid"))
                .andExpect(jsonPath("$.response[0].trackingNumber").value("TRACK123"));
    }

    @Test
    void updateDelivery_shouldReturnUpdated() throws Exception {
        DeliveryResponse response = DeliveryResponse.builder()
                .uuid("delivery-uuid").trackingNumber("TRACK456")
                .status("SHIPPED")
                .build();

        when(deliveryService.updateDelivery(eq("delivery-uuid"), any())).thenReturn(response);

        mockMvc.perform(put("/deliveries/delivery-uuid")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"carrierCode\":\"UPS\",\"status\":\"SHIPPED\",\"trackingNumber\":\"TRACK456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.status").value("SHIPPED"));
    }
}
