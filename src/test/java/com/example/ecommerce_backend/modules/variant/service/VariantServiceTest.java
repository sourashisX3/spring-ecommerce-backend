package com.example.ecommerce_backend.modules.variant.service;

import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.DuplicateSkuException;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.variant.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.variant.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import com.example.ecommerce_backend.modules.variant.exception.ProductVariantNotFoundException;
import com.example.ecommerce_backend.modules.variant.mapper.VariantMapper;
import com.example.ecommerce_backend.modules.variant.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VariantServiceTest {

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private VariantService variantService;

    private Product product;
    private ProductVariant variant;
    private ProductVariant defaultVariant;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L).uuid("product-uuid").name("Test Product")
                .slug("test-product").basePrice(BigDecimal.TEN).isActive(true)
                .variants(new ArrayList<>()).images(List.of()).tags(new java.util.HashSet<>())
                .build();

        variant = ProductVariant.builder()
                .id(1L).uuid("variant-uuid").sku("SKU-001").name("Red")
                .price(BigDecimal.valueOf(19.99)).stock(10)
                .attributes(new HashMap<>()).isDefault(false).sortOrder(1)
                .product(product).build();

        defaultVariant = ProductVariant.builder()
                .id(2L).uuid("default-uuid").sku("SKU-DEF").name("Default")
                .price(BigDecimal.valueOf(9.99)).stock(20)
                .attributes(new HashMap<>()).isDefault(true).sortOrder(0)
                .product(product).build();

        product.getVariants().add(variant);
        product.getVariants().add(defaultVariant);
    }

    // --- getVariants ---

    @Test
    void getVariants_shouldReturnList() {
        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));

        List<VariantResponse> result = variantService.getVariants("product-uuid");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(VariantResponse::getSku)
                .containsExactly("SKU-001", "SKU-DEF");
    }

    @Test
    void getVariants_whenProductNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variantService.getVariants("nonexistent"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- getVariant ---

    @Test
    void getVariant_shouldReturnVariant() {
        when(variantRepository.findByUuid("variant-uuid")).thenReturn(Optional.of(variant));

        VariantResponse result = variantService.getVariant("variant-uuid");

        assertThat(result.getSku()).isEqualTo("SKU-001");
    }

    @Test
    void getVariant_whenNotFound_shouldThrow() {
        when(variantRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variantService.getVariant("nonexistent"))
                .isInstanceOf(ProductVariantNotFoundException.class);
    }

    // --- addVariant ---

    @Test
    void addVariant_shouldSaveAndReturn() {
        VariantRequest request = new VariantRequest();
        request.setSku("SKU-NEW");
        request.setName("Green");
        request.setPrice(BigDecimal.valueOf(14.99));
        request.setStock(5);
        request.setSortOrder(2);
        request.setDefault(false);

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(variantRepository.existsBySku("SKU-NEW")).thenReturn(false);
        when(variantRepository.save(any())).thenAnswer(invocation -> {
            ProductVariant pv = invocation.getArgument(0);
            pv.setId(3L);
            pv.setUuid("new-uuid");
            return pv;
        });

        VariantResponse result = variantService.addVariant("product-uuid", request);

        assertThat(result.getSku()).isEqualTo("SKU-NEW");
        verify(variantRepository).save(any());
    }

    @Test
    void addVariant_whenProductNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variantService.addVariant("nonexistent", new VariantRequest()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void addVariant_whenDuplicateSku_shouldThrow() {
        VariantRequest request = new VariantRequest();
        request.setSku("SKU-001");

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(variantRepository.existsBySku("SKU-001")).thenReturn(true);

        assertThatThrownBy(() -> variantService.addVariant("product-uuid", request))
                .isInstanceOf(DuplicateSkuException.class);
    }

    @Test
    void addVariant_whenIsDefault_shouldUnsetOthers() {
        VariantRequest request = new VariantRequest();
        request.setSku("SKU-NEW");
        request.setName("Green");
        request.setPrice(BigDecimal.valueOf(14.99));
        request.setStock(5);
        request.setDefault(true);

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(variantRepository.existsBySku("SKU-NEW")).thenReturn(false);
        when(variantRepository.save(any())).thenAnswer(invocation -> {
            ProductVariant pv = invocation.getArgument(0);
            pv.setId(3L);
            pv.setUuid("new-uuid");
            return pv;
        });

        variantService.addVariant("product-uuid", request);

        assertThat(defaultVariant.isDefault()).isFalse();
    }

    // --- updateVariant ---

    @Test
    void updateVariant_shouldUpdateAndReturn() {
        VariantRequest request = new VariantRequest();
        request.setSku("SKU-001-UPD");
        request.setName("Red Updated");
        request.setPrice(BigDecimal.valueOf(24.99));
        request.setStock(15);
        request.setSortOrder(2);
        request.setDefault(false);

        when(variantRepository.findByUuid("variant-uuid")).thenReturn(Optional.of(variant));
        when(variantRepository.existsBySku("SKU-001-UPD")).thenReturn(false);
        when(variantRepository.save(any())).thenReturn(variant);

        VariantResponse result = variantService.updateVariant("variant-uuid", request);

        assertThat(result.getName()).isEqualTo("Red Updated");
        verify(variantRepository).save(variant);
    }

    @Test
    void updateVariant_whenNotFound_shouldThrow() {
        when(variantRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variantService.updateVariant("nonexistent", new VariantRequest()))
                .isInstanceOf(ProductVariantNotFoundException.class);
    }

    @Test
    void updateVariant_whenSkuChangedAndDuplicate_shouldThrow() {
        VariantRequest request = new VariantRequest();
        request.setSku("SKU-DEF");

        when(variantRepository.findByUuid("variant-uuid")).thenReturn(Optional.of(variant));
        when(variantRepository.existsBySku("SKU-DEF")).thenReturn(true);

        assertThatThrownBy(() -> variantService.updateVariant("variant-uuid", request))
                .isInstanceOf(DuplicateSkuException.class);
    }

    @Test
    void updateVariant_whenSameSku_shouldNotCheckDuplicate() {
        VariantRequest request = new VariantRequest();
        request.setSku("SKU-001");
        request.setName("Red Updated");
        request.setPrice(BigDecimal.valueOf(24.99));
        request.setStock(15);
        request.setDefault(false);

        when(variantRepository.findByUuid("variant-uuid")).thenReturn(Optional.of(variant));
        when(variantRepository.save(any())).thenReturn(variant);

        VariantResponse result = variantService.updateVariant("variant-uuid", request);

        assertThat(result.getName()).isEqualTo("Red Updated");
    }

    @Test
    void updateVariant_whenIsDefault_shouldUnsetOtherDefaults() {
        VariantRequest request = new VariantRequest();
        request.setSku("SKU-001");
        request.setName("Red");
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setStock(10);
        request.setDefault(true);

        when(variantRepository.findByUuid("variant-uuid")).thenReturn(Optional.of(variant));
        when(variantRepository.save(any())).thenReturn(variant);

        variantService.updateVariant("variant-uuid", request);

        assertThat(defaultVariant.isDefault()).isFalse();
    }

    // --- deleteVariant ---

    @Test
    void deleteVariant_shouldDelete() {
        when(variantRepository.findByUuid("variant-uuid")).thenReturn(Optional.of(variant));

        variantService.deleteVariant("variant-uuid");

        verify(variantRepository).delete(variant);
    }

    @Test
    void deleteVariant_whenNotFound_shouldThrow() {
        when(variantRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variantService.deleteVariant("nonexistent"))
                .isInstanceOf(ProductVariantNotFoundException.class);
    }
}
