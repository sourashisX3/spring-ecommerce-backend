package com.example.ecommerce_backend.modules.wallet.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.wallet.service.WalletTransactionTypeService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WalletTransactionTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletTransactionTypeService walletTransactionTypeService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    @BeforeEach
    void setUp() throws Throwable {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                try {
                    return ((org.aspectj.lang.ProceedingJoinPoint) invocation.getArgument(0)).proceed();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }).when(authorizationAspect).checkPermission(any(), any());
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        when(walletTransactionTypeService.toggleStatus("type-uuid", false)).thenReturn(true);

        mockMvc.perform(patch("/wallet-transaction-types/type-uuid/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wallet transaction type status updated successfully"));
    }

    @Test
    void toggleStatus_whenAlreadyInDesiredState_shouldReturnMessage() throws Exception {
        when(walletTransactionTypeService.toggleStatus("type-uuid", true)).thenReturn(false);

        mockMvc.perform(patch("/wallet-transaction-types/type-uuid/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wallet transaction type is already active"));
    }
}
