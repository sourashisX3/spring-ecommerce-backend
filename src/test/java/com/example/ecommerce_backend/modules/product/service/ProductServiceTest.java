package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import com.example.ecommerce_backend.modules.product.entity.*;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private ProductImageRepository imageRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ProductService productService;

    private Product activeProduct;
    private Product inactiveProduct;
    private Product otherProduct;
    private Category category;
    private Brand brand;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").slug("electronics").build();
        brand = Brand.builder().id(1L).name("TestBrand").slug("test-brand").build();

        activeProduct = Product.builder()
                .id(1L).uuid("uuid-active").sku("SKU-A").name("Active")
                .slug("active").basePrice(BigDecimal.TEN)
                .isActive(true).category(category).brand(brand)
                .variants(Collections.emptyList())
                .images(Collections.emptyList())
                .tags(new HashSet<>())
                .build();

        inactiveProduct = Product.builder()
                .id(2L).uuid("uuid-inactive").sku("SKU-I").name("Inactive")
                .slug("inactive").basePrice(BigDecimal.valueOf(20))
                .isActive(false).category(category).brand(brand)
                .variants(Collections.emptyList())
                .images(Collections.emptyList())
                .tags(new HashSet<>())
                .build();

        otherProduct = Product.builder()
                .id(3L).uuid("uuid-other").sku("SKU-O").name("Other")
                .slug("other").basePrice(BigDecimal.valueOf(15))
                .isActive(true).category(category).brand(brand)
                .variants(Collections.emptyList())
                .images(Collections.emptyList())
                .tags(new HashSet<>())
                .build();
    }

    // --- getAllProducts ---

    @Test
    void getAllProducts_shouldReturnPageWithActiveDefault() {
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(activeProduct)));

        Page<ProductResponse> result = productService.getAllProducts(
                null, null, null, null, null, null, null, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo("uuid-active");
    }

    @Test
    void getAllProducts_withActiveTrue_shouldReturnOnlyActive() {
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(activeProduct)));

        Page<ProductResponse> result = productService.getAllProducts(
                null, null, null, null, null, null, null, true, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isActive()).isTrue();
    }

    @Test
    void getAllProducts_withCategorySlug_shouldResolveDescendantIds() {
        when(categoryService.getDescendantCategoryIds("electronics")).thenReturn(Set.of(1L));
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(activeProduct)));

        Page<ProductResponse> result = productService.getAllProducts(
                "electronics", null, null, null, null, null, null, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAllProducts_withBrandSlug_shouldFilterByBrand() {
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(activeProduct)));

        Page<ProductResponse> result = productService.getAllProducts(
                null, "test-brand", null, null, null, null, null, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAllProducts_withAttributeFilters_shouldFilterInMemory() {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("color", "red");

        Product productWithAttr = Product.builder()
                .id(4L).uuid("uuid-attr").sku("SKU-ATTR").name("With Attr")
                .slug("with-attr").basePrice(BigDecimal.TEN)
                .isActive(true).category(category).brand(brand)
                .attributes(attrs)
                .variants(Collections.emptyList())
                .images(Collections.emptyList())
                .tags(new HashSet<>())
                .build();

        Product productNoAttr = Product.builder()
                .id(5L).uuid("uuid-noattr").sku("SKU-NOATTR").name("No Attr")
                .slug("no-attr").basePrice(BigDecimal.TEN)
                .isActive(true).category(category).brand(brand)
                .attributes(new HashMap<>())
                .variants(Collections.emptyList())
                .images(Collections.emptyList())
                .tags(new HashSet<>())
                .build();

        when(productRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(productNoAttr, productWithAttr)));

        Map<String, String> filters = Map.of("color", "red");
        Page<ProductResponse> result = productService.getAllProducts(
                null, null, null, null, null, null, null, null, filters, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo("uuid-attr");
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnProduct() {
        when(productRepository.findByUuid("uuid-active")).thenReturn(Optional.of(activeProduct));

        ProductResponse result = productService.getByUuid("uuid-active", null);

        assertThat(result.getUuid()).isEqualTo("uuid-active");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getByUuid("nonexistent", null))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnFalse() {
        when(productRepository.findByUuid("uuid-active")).thenReturn(Optional.of(activeProduct));

        boolean changed = productService.toggleStatus("uuid-active", true);

        assertThat(changed).isFalse();
        verify(productRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenAlreadyInactive_shouldReturnFalse() {
        when(productRepository.findByUuid("uuid-inactive")).thenReturn(Optional.of(inactiveProduct));

        boolean changed = productService.toggleStatus("uuid-inactive", false);

        assertThat(changed).isFalse();
        verify(productRepository, never()).save(any());
    }

    @Test
    void toggleStatus_shouldToggleActiveToInactive() {
        when(productRepository.findByUuid("uuid-active")).thenReturn(Optional.of(activeProduct));

        boolean changed = productService.toggleStatus("uuid-active", false);

        assertThat(changed).isTrue();
        assertThat(activeProduct.isActive()).isFalse();
        verify(productRepository).save(activeProduct);
    }

    @Test
    void toggleStatus_shouldToggleInactiveToActive() {
        when(productRepository.findByUuid("uuid-inactive")).thenReturn(Optional.of(inactiveProduct));

        boolean changed = productService.toggleStatus("uuid-inactive", true);

        assertThat(changed).isTrue();
        assertThat(inactiveProduct.isActive()).isTrue();
        verify(productRepository).save(inactiveProduct);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.toggleStatus("nonexistent", true))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // --- getSimilarProducts ---

    @Test
    void getSimilarProducts_shouldExcludeInactiveCandidates() {
        when(productRepository.findByUuid("uuid-active")).thenReturn(Optional.of(activeProduct));
        when(productRepository.findAll()).thenReturn(List.of(activeProduct, inactiveProduct, otherProduct));

        List<ProductResponse> result = productService.getSimilarProducts("uuid-active", 10);

        assertThat(result).allMatch(p -> !p.getUuid().equals("uuid-active"));
        assertThat(result).noneMatch(p -> p.getUuid().equals("uuid-inactive"));
    }

    @Test
    void getSimilarProducts_shouldExcludeSelf() {
        when(productRepository.findByUuid("uuid-active")).thenReturn(Optional.of(activeProduct));
        when(productRepository.findAll()).thenReturn(List.of(activeProduct, otherProduct));

        List<ProductResponse> result = productService.getSimilarProducts("uuid-active", 10);

        assertThat(result).noneMatch(p -> p.getUuid().equals("uuid-active"));
    }

    @Test
    void getSimilarProducts_shouldRespectLimit() {
        when(productRepository.findByUuid("uuid-active")).thenReturn(Optional.of(activeProduct));
        when(productRepository.findAll()).thenReturn(List.of(activeProduct, otherProduct));

        List<ProductResponse> result = productService.getSimilarProducts("uuid-active", 1);

        assertThat(result).hasSizeLessThanOrEqualTo(1);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteProduct() {
        when(productRepository.findByUuid("uuid-active")).thenReturn(Optional.of(activeProduct));

        productService.delete("uuid-active");

        verify(productRepository).delete(activeProduct);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete("nonexistent"))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
