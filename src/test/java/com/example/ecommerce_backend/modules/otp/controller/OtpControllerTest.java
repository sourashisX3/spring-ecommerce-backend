package com.example.ecommerce_backend.modules.otp.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.otp.service.OtpService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OtpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OtpService otpService;

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
    void sendOtp_shouldReturnSuccessMessage() throws Exception {
        when(otpService.generateOtp("john@test.com")).thenReturn("123456");

        mockMvc.perform(post("/otp/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"))
                .andExpect(jsonPath("$.response.message").value("OTP sent successfully"));
    }

    @Test
    void sendOtp_withMissingField_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/otp/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyOtp_withValidOtp_shouldReturnSuccess() throws Exception {
        when(otpService.validateOtp("john@test.com", "123456")).thenReturn(true);

        mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\",\"otp\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"))
                .andExpect(jsonPath("$.response.message").value("OTP verified successfully"));

        verify(otpService).invalidateOtp("john@test.com");
    }

    @Test
    void verifyOtp_withInvalidOtp_shouldReturnBadRequest() throws Exception {
        when(otpService.validateOtp("john@test.com", "wrong-otp")).thenReturn(false);

        mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\",\"otp\":\"wrong-otp\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or expired OTP"));

        verify(otpService, never()).invalidateOtp(anyString());
    }

    @Test
    void verifyOtp_withMissingFields_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailOrPhone\":\"john@test.com\"}"))
                .andExpect(status().isBadRequest());
    }
}
