package com.example.ecommerce_backend.modules.discount.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.discount.dto.request.DiscountTypeRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.discount.service.DiscountTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DiscountTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DiscountTypeService discountTypeService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

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
                .id(1L).uuid("dt-uuid-1").code("PERCENTAGE").name("Percentage")
                .description("Percentage discount").computation("PERCENTAGE")
                .configSchema("{}").isActive(true).build();
    }

    @Test
    void getAll_shouldReturnDiscountTypes() throws Exception {
        when(discountTypeService.getAll()).thenReturn(List.of(discountTypeResponse));

        mockMvc.perform(get("/api/discount-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].code").value("PERCENTAGE"));
    }

    @Test
    void getByUuid_shouldReturnDiscountType() throws Exception {
        when(discountTypeService.getByUuid("dt-uuid-1")).thenReturn(discountTypeResponse);

        mockMvc.perform(get("/api/discount-types/dt-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("PERCENTAGE"));
    }

    @Test
    void getByCode_shouldReturnDiscountType() throws Exception {
        when(discountTypeService.getByCode("PERCENTAGE")).thenReturn(discountTypeResponse);

        mockMvc.perform(get("/api/discount-types/code/PERCENTAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.code").value("PERCENTAGE"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        DiscountTypeRequest request = new DiscountTypeRequest();
        request.setCode("NEW_TYPE");
        request.setName("New Type");
        request.setDescription("New discount type");
        request.setComputation("PERCENTAGE");

        when(discountTypeService.create(any(DiscountTypeRequest.class))).thenReturn(discountTypeResponse);

        mockMvc.perform(post("/api/discount-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.code").value("PERCENTAGE"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        DiscountTypeRequest request = new DiscountTypeRequest();
        request.setCode("UPDATED");
        request.setName("Updated");
        request.setDescription("Updated type");
        request.setComputation("FIXED");

        when(discountTypeService.update(eq("dt-uuid-1"), any(DiscountTypeRequest.class)))
                .thenReturn(discountTypeResponse);

        mockMvc.perform(put("/api/discount-types/dt-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        mockMvc.perform(patch("/api/discount-types/dt-uuid-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Discount type status updated successfully"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/api/discount-types/dt-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Discount type deleted successfully"));
    }
}
