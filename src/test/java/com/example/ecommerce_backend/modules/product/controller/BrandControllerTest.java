package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.modules.product.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.product.service.BrandService;
import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.product.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.product.service.BrandService;
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
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService brandService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

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

    @Test
    void getAll_shouldReturnBrands() throws Exception {
        when(brandService.getAll(true)).thenReturn(List.of(
                BrandResponse.builder().id(1L).name("Test").slug("test").isActive(true).build()
        ));

        mockMvc.perform(get("/brands?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].slug").value("test"));
    }

    @Test
    void getAll_withoutActive_shouldCallServiceWithNull() throws Exception {
        when(brandService.getAll(null)).thenReturn(List.of(
                BrandResponse.builder().id(1L).name("Test").slug("test").build()
        ));

        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].slug").value("test"));
    }

    @Test
    void getBySlug_shouldReturnBrand() throws Exception {
        when(brandService.getBySlug("test")).thenReturn(
                BrandResponse.builder().id(1L).name("Test").slug("test").build()
        );

        mockMvc.perform(get("/brands/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name").value("Test"));
    }

    @Test
    void toggleStatus_whenChanged_shouldReturnSuccessMessage() throws Exception {
        when(brandService.toggleStatus("test", true)).thenReturn(true);

        mockMvc.perform(patch("/brands/test/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Brand status updated successfully"));
    }

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnAlreadyMessage() throws Exception {
        when(brandService.toggleStatus("test", true)).thenReturn(false);

        mockMvc.perform(patch("/brands/test/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Brand is already active"));
    }
}
