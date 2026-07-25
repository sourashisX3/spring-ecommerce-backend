package com.example.ecommerce_backend.modules.user.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

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
    void getAllUsers_shouldReturnPage() throws Exception {
        Page<UserResponse> page = new PageImpl<>(List.of(
                UserResponse.builder().id(1L).firstName("John").email("john@test.com").build()
        ));

        when(userService.getAllUsers(isNull(), isNull(), any())).thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].email").value("john@test.com"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void getAllUsers_withSearchAndActive_shouldPassParams() throws Exception {
        Page<UserResponse> page = new PageImpl<>(List.of(
                UserResponse.builder().id(1L).firstName("John").email("john@test.com").build()
        ));

        when(userService.getAllUsers(eq("john"), eq(true), any())).thenReturn(page);

        mockMvc.perform(get("/users?search=john&active=true"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(
                UserResponse.builder().id(1L).firstName("John").email("john@test.com").build()
        );

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.firstName").value("John"));
    }
}
