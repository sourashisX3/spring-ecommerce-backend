package com.example.ecommerce_backend.modules.coupon.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.coupon.dto.request.AssignCouponRequest;
import com.example.ecommerce_backend.modules.coupon.dto.request.CouponRequest;
import com.example.ecommerce_backend.modules.coupon.dto.request.CouponValidationRequest;
import com.example.ecommerce_backend.modules.coupon.dto.response.CouponResponse;
import com.example.ecommerce_backend.modules.coupon.service.CouponService;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private CouponResponse couponResponse;
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
        discountTypeResponse = DiscountTypeResponse.builder()
                .id(1L).uuid("dt-uuid").code("PERCENTAGE").name("Percentage")
                .computation("PERCENTAGE").isActive(true).build();

        couponResponse = CouponResponse.builder()
                .uuid("coupon-uuid-1").code("SAVE10")
                .discountType(discountTypeResponse)
                .discountValue(BigDecimal.TEN)
                .minOrderAmount(BigDecimal.valueOf(50))
                .maxDiscount(BigDecimal.valueOf(25))
                .usageLimit(100).usageLimitPerUser(5)
                .isActive(true).isGlobal(true)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
    }

    @Test
    void getAll_shouldReturnCoupons() throws Exception {
        when(couponService.getAll(isNull(), isNull(), isNull())).thenReturn(List.of(couponResponse));

        mockMvc.perform(get("/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].code").value("SAVE10"));
    }

    @Test
    void getAll_withFilters_shouldPassParams() throws Exception {
        when(couponService.getAll(eq(true), eq(false), isNull())).thenReturn(List.of(couponResponse));

        mockMvc.perform(get("/coupons?active=true&global=false"))
                .andExpect(status().isOk());
    }

    @Test
    void getByUuid_shouldReturnCoupon() throws Exception {
        when(couponService.getByUuid("coupon-uuid-1")).thenReturn(couponResponse);

        mockMvc.perform(get("/coupons/coupon-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("SAVE10"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        CouponRequest request = new CouponRequest();
        request.setCode("NEW10");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(couponService.create(any(CouponRequest.class))).thenReturn(couponResponse);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.code").value("SAVE10"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        CouponRequest request = new CouponRequest();
        request.setCode("UPDATED10");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.valueOf(20));
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(couponService.update(eq("coupon-uuid-1"), any(CouponRequest.class))).thenReturn(couponResponse);

        mockMvc.perform(put("/coupons/coupon-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        mockMvc.perform(patch("/coupons/coupon-uuid-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Coupon deactivated successfully"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/coupons/coupon-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Coupon deleted successfully"));
    }

    @Test
    void assignToUsers_shouldReturnSuccess() throws Exception {
        AssignCouponRequest assignRequest = new AssignCouponRequest();
        assignRequest.setUserUuids(List.of("user-uuid-1"));

        mockMvc.perform(post("/coupons/coupon-uuid-1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Coupon assigned successfully"));
    }

    @Test
    void removeAssignment_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/coupons/coupon-uuid-1/assign/user-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Assignment removed successfully"));
    }

    @Test
    void validateAndApply_shouldReturnDiscount() throws Exception {
        CouponValidationRequest validationRequest = new CouponValidationRequest();
        validationRequest.setCode("SAVE10");
        validationRequest.setUserId(1L);
        validationRequest.setOrderSubtotal(BigDecimal.valueOf(100));

        when(couponService.validateAndApply(eq("SAVE10"), eq(1L), eq(BigDecimal.valueOf(100)), isNull()))
                .thenReturn(BigDecimal.TEN);

        mockMvc.perform(post("/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(10));
    }
}
