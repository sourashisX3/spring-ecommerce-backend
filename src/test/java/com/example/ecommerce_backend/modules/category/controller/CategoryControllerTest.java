package com.example.ecommerce_backend.modules.category.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

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
    void getAll_shouldReturnCategories() throws Exception {
        when(categoryService.getAll(true)).thenReturn(List.of(
                CategoryResponse.builder().id(1L).name("Test").slug("test").isActive(true).build()
        ));

        mockMvc.perform(get("/categories?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Categories retrieved successfully"))
                .andExpect(jsonPath("$.response[0].slug").value("test"));
    }

    @Test
    void getAll_withoutActive_shouldCallServiceWithNull() throws Exception {
        when(categoryService.getAll(null)).thenReturn(List.of(
                CategoryResponse.builder().id(1L).name("Test").slug("test").build()
        ));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].slug").value("test"));
    }

    @Test
    void getTree_shouldReturnTree() throws Exception {
        when(categoryService.getTree(null)).thenReturn(List.of(
                CategoryResponse.builder().id(1L).name("Root").slug("root").build()
        ));

        mockMvc.perform(get("/categories/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].slug").value("root"));
    }

    @Test
    void getTree_withActiveParam_shouldCallServiceWithActive() throws Exception {
        when(categoryService.getTree(true)).thenReturn(List.of());

        mockMvc.perform(get("/categories/tree?active=true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBySlug_shouldReturnCategory() throws Exception {
        when(categoryService.getBySlug("test")).thenReturn(
                CategoryResponse.builder().id(1L).name("Test").slug("test").build()
        );

        mockMvc.perform(get("/categories/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name").value("Test"));
    }

    @Test
    void toggleStatus_whenChanged_shouldReturnSuccessMessage() throws Exception {
        when(categoryService.toggleStatus("test", true)).thenReturn(true);

        mockMvc.perform(patch("/categories/test/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category status updated successfully"));
    }

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnAlreadyMessage() throws Exception {
        when(categoryService.toggleStatus("test", true)).thenReturn(false);

        mockMvc.perform(patch("/categories/test/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category is already active"));
    }

    @Test
    void toggleStatus_whenAlreadyInactive_shouldReturnAlreadyMessage() throws Exception {
        when(categoryService.toggleStatus("test", false)).thenReturn(false);

        mockMvc.perform(patch("/categories/test/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category is already inactive"));
    }
}
