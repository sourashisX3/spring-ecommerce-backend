package com.example.ecommerce_backend.modules.auth.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.auth.dto.response.AuthResponse;
import com.example.ecommerce_backend.modules.auth.service.AuthService;
import com.example.ecommerce_backend.modules.user.dto.response.UserResponse;
import org.aspectj.lang.ProceedingJoinPoint;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

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
    void register_shouldReturnCreatedAndAuthResponse() throws Exception {
        AuthResponse authResponse = AuthResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(UserResponse.builder().id(1L).firstName("John").email("john@test.com").build())
                .build();

        when(authService.register(any())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@test.com\",\"dialCode\":\"+1\",\"phoneNumber\":\"1234567890\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.response.token").value("access-token"))
                .andExpect(jsonPath("$.response.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.response.user.firstName").value("John"));
    }

    @Test
    void register_withMissingFields_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"invalid\",\"dialCode\":\"+1\",\"phoneNumber\":\"1234567890\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnOkAndAuthResponse() throws Exception {
        AuthResponse authResponse = AuthResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(UserResponse.builder().id(1L).firstName("John").email("john@test.com").build())
                .build();

        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.response.token").value("access-token"));
    }

    @Test
    void login_withMissingFields_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_shouldReturnOkAndAuthResponse() throws Exception {
        AuthResponse authResponse = AuthResponse.builder()
                .token("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(UserResponse.builder().id(1L).firstName("John").build())
                .build();

        when(authService.refresh(any())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"old-refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.response.token").value("new-access-token"))
                .andExpect(jsonPath("$.response.refreshToken").value("new-refresh-token"));
    }

    @Test
    void refresh_withMissingToken_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendOtp_shouldReturnOtp() throws Exception {
        when(authService.sendOtp(any())).thenReturn("123456");

        mockMvc.perform(post("/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"))
                .andExpect(jsonPath("$.response.otp").value("123456"));
    }

    @Test
    void sendOtp_withMissingField_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyOtp_shouldReturnAuthResponse() throws Exception {
        AuthResponse authResponse = AuthResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(UserResponse.builder().id(1L).firstName("John").build())
                .build();

        when(authService.verifyOtp(any())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\",\"otp\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"))
                .andExpect(jsonPath("$.response.token").value("access-token"));
    }

    @Test
    void verifyOtp_withMissingFields_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_withoutAuthentication_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        verify(authService, never()).logout(anyString());
    }

    @Test
    void logout_withAuthenticatedUser_shouldCallService() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .with(user("john@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        verify(authService).logout("john@test.com");
    }
}
