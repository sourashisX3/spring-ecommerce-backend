package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnPage() throws Exception {
        Page<ProductResponse> page = new PageImpl<>(List.of(
                ProductResponse.builder().uuid("abc").name("Test").slug("test")
                        .basePrice(BigDecimal.TEN).build()
        ));

        when(productService.getAllProducts(isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("abc"))
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void getAllProducts_withActiveParam_shouldPassToService() throws Exception {
        Page<ProductResponse> page = new PageImpl<>(Collections.emptyList());

        when(productService.getAllProducts(isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq(true), isNull(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/products?active=true"))
                .andExpect(status().isOk());
    }

    @Test
    void getByUuid_shouldReturnProduct() throws Exception {
        when(productService.getByUuid("abc", null)).thenReturn(
                ProductResponse.builder().uuid("abc").name("Test").slug("test")
                        .basePrice(BigDecimal.TEN).build()
        );

        mockMvc.perform(get("/products/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.uuid").value("abc"));
    }

    @Test
    void getSimilarProducts_shouldReturnList() throws Exception {
        when(productService.getSimilarProducts("abc", 10)).thenReturn(List.of(
                ProductResponse.builder().uuid("similar").name("Similar").slug("similar")
                        .basePrice(BigDecimal.TEN).build()
        ));

        mockMvc.perform(get("/products/abc/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].uuid").value("similar"));
    }

    @Test
    void toggleStatus_whenChanged_shouldReturnSuccessMessage() throws Exception {
        when(productService.toggleStatus("abc", true)).thenReturn(true);

        mockMvc.perform(patch("/products/abc/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product status updated successfully"));
    }

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnAlreadyMessage() throws Exception {
        when(productService.toggleStatus("abc", true)).thenReturn(false);

        mockMvc.perform(patch("/products/abc/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product is already active"));
    }
}
