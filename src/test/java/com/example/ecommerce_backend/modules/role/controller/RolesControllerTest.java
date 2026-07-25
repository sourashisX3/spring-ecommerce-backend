package com.example.ecommerce_backend.modules.role.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.service.RolesService;
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
class RolesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolesService rolesService;

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
    void getAllRoles_shouldReturnPage() throws Exception {
        Page<RolesResponse> page = new PageImpl<>(List.of(
                RolesResponse.builder().id(1L).roleName("ADMIN").build()
        ));

        when(rolesService.getAllRoles(isNull(), any())).thenReturn(page);

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].roleName").value("ADMIN"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void getAllRoles_withSearch_shouldPassParam() throws Exception {
        Page<RolesResponse> page = new PageImpl<>(List.of(
                RolesResponse.builder().id(1L).roleName("ADMIN").build()
        ));

        when(rolesService.getAllRoles(eq("admin"), any())).thenReturn(page);

        mockMvc.perform(get("/roles?search=admin"))
                .andExpect(status().isOk());
    }
}
