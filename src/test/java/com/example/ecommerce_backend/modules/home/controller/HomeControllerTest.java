package com.example.ecommerce_backend.modules.home.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.home.dto.DashboardResponse;
import com.example.ecommerce_backend.modules.home.dto.HomeResponse;
import com.example.ecommerce_backend.modules.home.service.HomeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HomeService homeService;

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
    void getHome_shouldReturnHomeData() throws Exception {
        HomeResponse homeResponse = HomeResponse.builder()
                .categories(List.of())
                .brands(List.of())
                .newArrivals(List.of())
                .featuredProducts(List.of())
                .build();

        when(homeService.getHomeData(any())).thenReturn(homeResponse);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Home data retrieved successfully"));
    }

    @Test
    void getDashboard_shouldReturnDashboardData() throws Exception {
        DashboardResponse dashboardResponse = DashboardResponse.builder()
                .orderCount(5)
                .wishlistCount(3)
                .cartCount(2)
                .walletBalance(BigDecimal.valueOf(100.00))
                .unreadNotificationCount(1)
                .recentOrders(List.of())
                .build();

        when(homeService.getDashboard(any())).thenReturn(dashboardResponse);

        mockMvc.perform(get("/home/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.orderCount").value(5))
                .andExpect(jsonPath("$.response.walletBalance").value(100.00));
    }
}
