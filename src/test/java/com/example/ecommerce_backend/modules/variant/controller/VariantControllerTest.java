package com.example.ecommerce_backend.modules.variant.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.variant.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.variant.service.VariantService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VariantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VariantService variantService;

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
    void getVariants_shouldReturnList() throws Exception {
        List<VariantResponse> variants = List.of(
                VariantResponse.builder()
                        .uuid("variant-uuid").sku("SKU-001").name("Red")
                        .price(BigDecimal.valueOf(19.99)).stock(10).build()
        );

        when(variantService.getVariants("product-uuid")).thenReturn(variants);

        mockMvc.perform(get("/products/product-uuid/variants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].sku").value("SKU-001"));
    }

    @Test
    void getVariant_shouldReturnVariant() throws Exception {
        VariantResponse variant = VariantResponse.builder()
                .uuid("variant-uuid").sku("SKU-001").name("Red")
                .price(BigDecimal.valueOf(19.99)).stock(10).build();

        when(variantService.getVariant("variant-uuid")).thenReturn(variant);

        mockMvc.perform(get("/variants/variant-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.sku").value("SKU-001"));
    }

    @Test
    void addVariant_shouldReturnCreated() throws Exception {
        VariantResponse variant = VariantResponse.builder()
                .uuid("new-variant-uuid").sku("SKU-002").name("Blue")
                .price(BigDecimal.valueOf(24.99)).stock(5).build();

        when(variantService.addVariant(eq("product-uuid"), any())).thenReturn(variant);

        mockMvc.perform(post("/products/product-uuid/variants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sku": "SKU-002",
                                    "name": "Blue",
                                    "price": 24.99,
                                    "stock": 5,
                                    "isDefault": false,
                                    "sortOrder": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.sku").value("SKU-002"));
    }

    @Test
    void updateVariant_shouldReturnSuccess() throws Exception {
        VariantResponse variant = VariantResponse.builder()
                .uuid("variant-uuid").sku("SKU-001").name("Red Updated")
                .price(BigDecimal.valueOf(29.99)).stock(15).build();

        when(variantService.updateVariant(eq("variant-uuid"), any())).thenReturn(variant);

        mockMvc.perform(put("/variants/variant-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "sku": "SKU-001",
                                    "name": "Red Updated",
                                    "price": 29.99,
                                    "stock": 15,
                                    "isDefault": true,
                                    "sortOrder": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name").value("Red Updated"));
    }

    @Test
    void deleteVariant_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/variants/variant-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Variant deleted successfully"));
    }
}
