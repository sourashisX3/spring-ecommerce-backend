package com.example.ecommerce_backend.modules.userpermission.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.userpermission.dto.response.UserPermissionResponse;
import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import com.example.ecommerce_backend.modules.userpermission.service.UserPermissionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserPermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserPermissionService userPermissionService;

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
    void getUserPermissions_shouldReturnPage() throws Exception {
        Page<UserPermissionResponse> page = new PageImpl<>(List.of(
                UserPermissionResponse.builder()
                        .id(1L).userId(1L).permissionName("product:read")
                        .effect(UserPermission.Effect.GRANT).build()
        ));

        when(userPermissionService.getUserPermissions(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/users/1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].permissionName").value("product:read"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void assignPermission_shouldReturnCreated() throws Exception {
        UserPermissionResponse response = UserPermissionResponse.builder()
                .id(1L).userId(1L).permissionName("product:write")
                .effect(UserPermission.Effect.GRANT).build();

        when(userPermissionService.assignPermission(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/users/1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "permissionId": 2,
                                    "effect": "GRANT"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.permissionName").value("product:write"));
    }

    @Test
    void removePermission_shouldReturnSuccess() throws Exception {
        doNothing().when(userPermissionService).removePermission(1L);

        mockMvc.perform(delete("/users/1/permissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Permission removed successfully"));
    }
}
