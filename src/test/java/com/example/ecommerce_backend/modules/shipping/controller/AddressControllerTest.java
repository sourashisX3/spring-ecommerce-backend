package com.example.ecommerce_backend.modules.shipping.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.shipping.dto.request.AddressRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.AddressResponse;
import com.example.ecommerce_backend.modules.shipping.service.ShippingAddressService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShippingAddressService shippingAddressService;

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

    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
    }

    @Test
    void getAddresses_shouldReturnList() throws Exception {
        AddressResponse address = AddressResponse.builder()
                .uuid("addr-uuid").recipientName("John Doe").city("New York")
                .build();

        when(shippingAddressService.getAddresses(1L)).thenReturn(List.of(address));

        mockMvc.perform(get("/addresses").with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("addr-uuid"));
    }

    @Test
    void getByUuid_shouldReturnAddress() throws Exception {
        AddressResponse address = AddressResponse.builder()
                .uuid("addr-uuid").recipientName("John Doe")
                .build();

        when(shippingAddressService.getByUuid("addr-uuid", 1L)).thenReturn(address);

        mockMvc.perform(get("/addresses/addr-uuid").with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("addr-uuid"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        AddressResponse address = AddressResponse.builder()
                .uuid("addr-uuid").recipientName("John Doe")
                .build();

        when(shippingAddressService.create(any(AddressRequest.class), eq(1L))).thenReturn(address);

        mockMvc.perform(post("/addresses").with(authentication(auth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipientName\":\"John Doe\",\"phone\":\"1234567890\",\"addressLine1\":\"123 Main St\",\"city\":\"New York\",\"state\":\"NY\",\"postalCode\":\"10001\",\"country\":\"USA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.uuid").value("addr-uuid"));
    }

    @Test
    void create_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/addresses").with(authentication(auth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        AddressResponse address = AddressResponse.builder()
                .uuid("addr-uuid").recipientName("Jane Doe")
                .build();

        when(shippingAddressService.update(eq("addr-uuid"), any(AddressRequest.class), eq(1L)))
                .thenReturn(address);

        mockMvc.perform(put("/addresses/addr-uuid").with(authentication(auth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipientName\":\"Jane Doe\",\"phone\":\"9876543210\",\"addressLine1\":\"456 Oak St\",\"city\":\"Los Angeles\",\"state\":\"CA\",\"postalCode\":\"90001\",\"country\":\"USA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.recipientName").value("Jane Doe"));
    }

    @Test
    void setDefault_shouldReturnSuccess() throws Exception {
        doNothing().when(shippingAddressService).setDefault("addr-uuid", 1L);

        mockMvc.perform(patch("/addresses/addr-uuid/default").with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Default address updated successfully"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        doNothing().when(shippingAddressService).delete("addr-uuid", 1L);

        mockMvc.perform(delete("/addresses/addr-uuid").with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Address deleted successfully"));
    }
}
