package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.modules.product.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.product.service.TagService;
import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.product.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.product.service.TagService;
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
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

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
    void getAll_shouldReturnTags() throws Exception {
        when(tagService.getAll(true)).thenReturn(List.of(
                TagResponse.builder().id(1L).name("New").slug("new").isActive(true).build()
        ));

        mockMvc.perform(get("/tags?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].slug").value("new"));
    }

    @Test
    void getAll_withoutActive_shouldCallServiceWithNull() throws Exception {
        when(tagService.getAll(null)).thenReturn(List.of(
                TagResponse.builder().id(1L).name("New").slug("new").build()
        ));

        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].slug").value("new"));
    }

    @Test
    void toggleStatus_whenChanged_shouldReturnSuccessMessage() throws Exception {
        when(tagService.toggleStatus("new", true)).thenReturn(true);

        mockMvc.perform(patch("/tags/new/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tag status updated successfully"));
    }

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnAlreadyMessage() throws Exception {
        when(tagService.toggleStatus("new", true)).thenReturn(false);

        mockMvc.perform(patch("/tags/new/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tag is already active"));
    }
}
