package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.modules.product.dto.response.WishlistItemResponse;
import com.example.ecommerce_backend.modules.product.service.WishlistService;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistService wishlistService;

    private WishlistItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        itemResponse = WishlistItemResponse.builder()
                .uuid("wishlist-uuid-1")
                .productUuid("product-uuid-1")
                .productName("Test Product")
                .productSlug("test-product")
                .productImage("http://example.com/image.jpg")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void getWishlist_shouldReturnList() throws Exception {
        when(wishlistService.getWishlist(any())).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.response[0].uuid").value("wishlist-uuid-1"))
                .andExpect(jsonPath("$.response[0].productUuid").value("product-uuid-1"))
                .andExpect(jsonPath("$.response[0].productName").value("Test Product"));
    }

    @Test
    void getWishlist_whenEmpty_shouldReturnEmptyList() throws Exception {
        when(wishlistService.getWishlist(any())).thenReturn(List.of());

        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.response").isArray())
                .andExpect(jsonPath("$.response.length()").value(0));
    }

    @Test
    void addToWishlist_shouldReturnCreated() throws Exception {
        when(wishlistService.addToWishlist(eq("product-uuid-1"), any()))
                .thenReturn(itemResponse);

        mockMvc.perform(post("/wishlist/{productUuid}", "product-uuid-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.response.uuid").value("wishlist-uuid-1"))
                .andExpect(jsonPath("$.response.productUuid").value("product-uuid-1"));
    }

    @Test
    void deleteWishlistItem_shouldReturnSuccess() throws Exception {
        doNothing().when(wishlistService).removeFromWishlist(eq("wishlist-uuid-1"), any());

        mockMvc.perform(delete("/wishlist/{itemUuid}", "wishlist-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Removed from wishlist successfully"));
    }
}