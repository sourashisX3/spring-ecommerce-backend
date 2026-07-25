package com.example.ecommerce_backend.modules.cart.controller;

import com.example.ecommerce_backend.modules.cart.dto.request.CartItemRequest;
import com.example.ecommerce_backend.modules.cart.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    private CartItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        itemResponse = CartItemResponse.builder()
                .uuid("cart-uuid-1")
                .productUuid("product-uuid-1")
                .productName("Test Product")
                .productSlug("test-product")
                .productImage("http://example.com/image.jpg")
                .productPrice(BigDecimal.valueOf(29.99))
                .quantity(2)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void getCart_shouldReturnList() throws Exception {
        when(cartService.getCart(any())).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.response[0].uuid").value("cart-uuid-1"))
                .andExpect(jsonPath("$.response[0].productUuid").value("product-uuid-1"))
                .andExpect(jsonPath("$.response[0].quantity").value(2));
    }

    @Test
    void getCart_whenEmpty_shouldReturnEmptyList() throws Exception {
        when(cartService.getCart(any())).thenReturn(List.of());

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()").value(0));
    }

    @Test
    void addToCart_shouldReturnCreated() throws Exception {
        when(cartService.addToCart(eq("product-uuid-1"), any(CartItemRequest.class), any()))
                .thenReturn(itemResponse);

        mockMvc.perform(post("/cart/{productUuid}", "product-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.response.uuid").value("cart-uuid-1"))
                .andExpect(jsonPath("$.response.quantity").value(2));
    }

    @Test
    void updateQuantity_shouldReturnSuccess() throws Exception {
        CartItemResponse updated = CartItemResponse.builder()
                .uuid("cart-uuid-1")
                .productUuid("product-uuid-1")
                .productName("Test Product")
                .productSlug("test-product")
                .quantity(5)
                .build();

        when(cartService.updateQuantity(eq("cart-uuid-1"), any(CartItemRequest.class), any()))
                .thenReturn(updated);

        mockMvc.perform(patch("/cart/{itemUuid}", "cart-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.response.uuid").value("cart-uuid-1"))
                .andExpect(jsonPath("$.response.quantity").value(5));
    }

    @Test
    void removeFromCart_shouldReturnSuccess() throws Exception {
        doNothing().when(cartService).removeFromCart(eq("cart-uuid-1"), any());

        mockMvc.perform(delete("/cart/{itemUuid}", "cart-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Removed from cart successfully"));
    }

    @Test
    void clearCart_shouldReturnSuccess() throws Exception {
        doNothing().when(cartService).clearCart(any());

        mockMvc.perform(delete("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Cart cleared successfully"));
    }
}
