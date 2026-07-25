package com.example.ecommerce_backend.modules.permission.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.permission.dto.request.CreatePermissionRequest;
import com.example.ecommerce_backend.modules.permission.dto.response.PermissionResponse;
import com.example.ecommerce_backend.modules.permission.service.PermissionService;
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
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

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
    void getAllPermissions_shouldReturnPage() throws Exception {
        Page<PermissionResponse> page = new PageImpl<>(List.of(
                PermissionResponse.builder().id(1L).permissionName("product:read").permissionDescription("Read products").build()
        ));

        when(permissionService.getAllPermissions(any())).thenReturn(page);

        mockMvc.perform(get("/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].permissionName").value("product:read"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void createPermission_shouldReturnCreated() throws Exception {
        PermissionResponse response = PermissionResponse.builder()
                .id(1L).permissionName("product:write").permissionDescription("Write products").build();

        when(permissionService.createPermission(any())).thenReturn(response);

        mockMvc.perform(post("/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "permissionName": "product:write",
                                    "permissionDescription": "Write products"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.permissionName").value("product:write"));
    }

    @Test
    void deletePermission_shouldReturnSuccess() throws Exception {
        doNothing().when(permissionService).deletePermission(1L);

        mockMvc.perform(delete("/permissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Permission deleted successfully"));
    }
}
