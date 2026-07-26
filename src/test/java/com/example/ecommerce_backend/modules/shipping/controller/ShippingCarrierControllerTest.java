package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
import com.example.ecommerce_backend.modules.shipping.service.ShippingCarrierService;
import com.example.ecommerce_backend.modules.user.entity.User;
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
class ShippingCarrierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShippingCarrierService shippingCarrierService;

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
    void getAll_shouldReturnList() throws Exception {
        ShippingCarrierResponse carrier = ShippingCarrierResponse.builder()
                .uuid("carrier-uuid")
                .code("UPS")
                .name("UPS")
                .isActive(true)
                .build();

        when(shippingCarrierService.getAll()).thenReturn(List.of(carrier));

        mockMvc.perform(get("/shipping-carriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].code").value("UPS"));
    }

    @Test
    void getByUuid_shouldReturnCarrier() throws Exception {
        ShippingCarrierResponse carrier = ShippingCarrierResponse.builder()
                .uuid("carrier-uuid")
                .code("UPS")
                .name("UPS")
                .build();

        when(shippingCarrierService.getByUuid("carrier-uuid")).thenReturn(carrier);

        mockMvc.perform(get("/shipping-carriers/carrier-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("UPS"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        ShippingCarrierResponse response = ShippingCarrierResponse.builder()
                .uuid("carrier-uuid")
                .code("FEDEX")
                .name("FedEx")
                .build();

        when(shippingCarrierService.create(any())).thenReturn(response);

        mockMvc.perform(post("/shipping-carriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"FEDEX\",\"name\":\"FedEx\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.code").value("FEDEX"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        ShippingCarrierResponse response = ShippingCarrierResponse.builder()
                .uuid("carrier-uuid")
                .code("UPS")
                .name("UPS Updated")
                .build();

        when(shippingCarrierService.update(eq("carrier-uuid"), any())).thenReturn(response);

        mockMvc.perform(put("/shipping-carriers/carrier-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"UPS\",\"name\":\"UPS Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name").value("UPS Updated"));
    }

    @Test
    void toggleStatus_shouldReturnToggled() throws Exception {
        ShippingCarrierResponse response = ShippingCarrierResponse.builder()
                .uuid("carrier-uuid")
                .code("UPS")
                .isActive(false)
                .build();

        when(shippingCarrierService.toggleStatus("carrier-uuid")).thenReturn(response);

        mockMvc.perform(patch("/shipping-carriers/carrier-uuid/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.isActive").value(false));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        doNothing().when(shippingCarrierService).delete("carrier-uuid");

        mockMvc.perform(delete("/shipping-carriers/carrier-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Shipping carrier deleted successfully"));
    }

    @Test
    void create_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/shipping-carriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
