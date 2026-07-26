package com.example.ecommerce_backend.modules.wallet.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletTransactionResponse;
import com.example.ecommerce_backend.modules.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    private User testUser;

    @BeforeEach
    void setUp() throws Throwable {
        testUser = User.builder()
                .id(1L).uuid("user-uuid")
                .email("test@test.com")
                .firstName("Test").lastName("User")
                .build();

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
    void getWallet_shouldReturnWallet() throws Exception {
        WalletResponse wallet = WalletResponse.builder()
                .id(1L).uuid("wallet-uuid")
                .balance(BigDecimal.valueOf(100))
                .currency("USD").isActive(true)
                .build();

        when(walletService.getWallet(1L)).thenReturn(wallet);

        mockMvc.perform(get("/wallet")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("wallet-uuid"))
                .andExpect(jsonPath("$.response.balance").value(100));
    }

    @Test
    void getTransactions_withoutPagination_shouldReturnList() throws Exception {
        WalletTransactionResponse txn = WalletTransactionResponse.builder()
                .id(1L).uuid("txn-uuid")
                .type("CREDIT").amount(BigDecimal.TEN)
                .balanceBefore(BigDecimal.ZERO).balanceAfter(BigDecimal.TEN)
                .build();

        when(walletService.getTransactions(1L)).thenReturn(List.of(txn));

        mockMvc.perform(get("/wallet/transactions")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("txn-uuid"));
    }

    @Test
    void getTransactions_withPagination_shouldReturnPaginated() throws Exception {
        WalletTransactionResponse txn = WalletTransactionResponse.builder()
                .id(1L).uuid("txn-uuid")
                .type("CREDIT").amount(BigDecimal.TEN)
                .balanceBefore(BigDecimal.ZERO).balanceAfter(BigDecimal.TEN)
                .build();

        Page<WalletTransactionResponse> page = new PageImpl<>(List.of(txn));
        when(walletService.getTransactions(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/wallet/transactions?page=0&size=10")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("txn-uuid"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        when(walletService.toggleStatus("wallet-uuid", false)).thenReturn(true);

        mockMvc.perform(patch("/wallet/wallet-uuid/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Wallet status updated successfully"));
    }
}
