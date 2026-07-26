package com.example.ecommerce_backend.modules.discount.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.discount.dto.request.DiscountRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountResponse;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.discount.service.DiscountService;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.lang.ProceedingJoinPoint;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private DiscountService discountService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private User testUser;
    private DiscountResponse discountResponse;
    private DiscountTypeResponse discountTypeResponse;

    @BeforeEach
    void setUpAuth() throws Throwable {
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

    @BeforeEach
    void setUpData() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("pass")
                .isActive(true)
                .build();

        discountTypeResponse = DiscountTypeResponse.builder()
                .id(1L).uuid("dt-uuid").code("PERCENTAGE").name("Percentage")
                .computation("PERCENTAGE").isActive(true).build();

        discountResponse = DiscountResponse.builder()
                .id(1L).uuid("discount-uuid-1")
                .discountType(discountTypeResponse)
                .discountValue(BigDecimal.TEN)
                .minOrderAmount(BigDecimal.valueOf(50))
                .maxDiscount(BigDecimal.valueOf(25))
                .isActive(true).isGlobal(true)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .description("10% off")
                .build();
    }

    @Test
    void getAll_shouldReturnDiscounts() throws Exception {
        when(discountService.getAll(null, null)).thenReturn(List.of(discountResponse));

        mockMvc.perform(get("/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].description").value("10% off"));
    }

    @Test
    void getAll_withFilters_shouldPassParams() throws Exception {
        when(discountService.getAll(eq(true), eq(false))).thenReturn(List.of(discountResponse));

        mockMvc.perform(get("/discounts?active=true&global=false"))
                .andExpect(status().isOk());
    }

    @Test
    void getByUuid_shouldReturnDiscount() throws Exception {
        when(discountService.getByUuid("discount-uuid-1")).thenReturn(discountResponse);

        mockMvc.perform(get("/discounts/discount-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.description").value("10% off"));
    }

    @Test
    void getEligible_shouldReturnEligibleDiscounts() throws Exception {
        when(discountService.getEligibleDiscounts(1L)).thenReturn(List.of(discountResponse));

        mockMvc.perform(get("/discounts/eligible")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].description").value("10% off"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        DiscountRequest request = new DiscountRequest();
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountService.create(any(DiscountRequest.class))).thenReturn(discountResponse);

        mockMvc.perform(post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.description").value("10% off"));
    }

    @Test
    void create_withUserUuids_shouldCallCreateAssignable() throws Exception {
        DiscountRequest request = new DiscountRequest();
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        request.setUserUuids(List.of("user-uuid-1"));

        when(discountService.createAssignable(any(DiscountRequest.class), anyList()))
                .thenReturn(discountResponse);

        mockMvc.perform(post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        DiscountRequest request = new DiscountRequest();
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.valueOf(20));
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(discountService.update(eq("discount-uuid-1"), any(DiscountRequest.class)))
                .thenReturn(discountResponse);

        mockMvc.perform(put("/discounts/discount-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        mockMvc.perform(patch("/discounts/discount-uuid-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Discount status updated successfully"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/discounts/discount-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Discount deleted successfully"));
    }

    @Test
    void assignToUsers_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/discounts/discount-uuid-1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("user-uuid-1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Discount assigned to users successfully"));
    }

    @Test
    void removeAssignment_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/discounts/discount-uuid-1/assign/user-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Assignment removed successfully"));
    }
}
