package com.example.ecommerce_backend.modules.product.specification;

import com.example.ecommerce_backend.modules.brand.entity.Brand;
import com.example.ecommerce_backend.modules.category.entity.Category;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.tag.entity.Tag;
import com.example.ecommerce_backend.modules.category.repository.CategoryRepository;
import com.example.ecommerce_backend.modules.brand.repository.BrandRepository;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.tag.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductSpecificationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TagRepository tagRepository;

    private Category category;
    private Brand brand;
    private Tag tag;
    private Product activeProduct;
    private Product inactiveProduct;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(
                Category.builder().name("Electronics").slug("electronics").build());

        brand = brandRepository.save(
                Brand.builder().name("TestBrand").slug("test-brand").build());

        tag = tagRepository.save(
                Tag.builder().name("New").slug("new").build());

        activeProduct = productRepository.save(Product.builder()
                .sku("SKU-ACTIVE").name("Active Product").slug("active-product")
                .basePrice(BigDecimal.valueOf(100))
                .category(category).brand(brand)
                .isActive(true).build());

        inactiveProduct = productRepository.save(Product.builder()
                .sku("SKU-INACTIVE").name("Retired Product").slug("retired-product")
                .basePrice(BigDecimal.valueOf(200))
                .category(categoryRepository.save(
                        Category.builder().name("Home").slug("home").build()))
                .brand(brandRepository.save(
                        Brand.builder().name("OtherBrand").slug("other-brand").build()))
                .isActive(false).build());
    }

    @Test
    void withFilters_whenActiveNull_shouldReturnAllProducts() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, null, null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(2);
    }

    @Test
    void withFilters_whenActiveTrue_shouldReturnOnlyActive() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, null, null, null, null, true);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-ACTIVE");
    }

    @Test
    void withFilters_whenActiveFalse_shouldReturnOnlyInactive() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, null, null, null, null, false);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-INACTIVE");
    }

    @Test
    void withFilters_whenCategoryIdsProvided_shouldFilterByCategory() {
        Specification<Product> spec = ProductSpecification.withFilters(
                Set.of(category.getId()), null, null, null, null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
    }

    @Test
    void withFilters_whenCategoryIdsEmpty_shouldReturnNoResults() {
        Specification<Product> spec = ProductSpecification.withFilters(
                Set.of(), null, null, null, null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).isEmpty();
    }

    @Test
    void withFilters_whenBrandSlugProvided_shouldFilterByBrand() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, "test-brand", null, null, null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
    }

    @Test
    void withFilters_whenBrandSlugDoesNotMatch_shouldReturnNoResults() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, "non-existent-brand", null, null, null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).isEmpty();
    }

    @Test
    void withFilters_whenSearchProvided_shouldMatchByName() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, "active", null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-ACTIVE");
    }

    @Test
    void withFilters_whenSearchDoesNotMatch_shouldReturnNoResults() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, "nonexistent", null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).isEmpty();
    }

    @Test
    void withFilters_whenMinPriceProvided_shouldFilterByMinPrice() {
        Product expensive = productRepository.save(Product.builder()
                .sku("SKU-EXPENSIVE").name("Expensive Product").slug("expensive-product")
                .basePrice(BigDecimal.valueOf(300))
                .category(category).brand(brand)
                .isActive(true).build());

        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, null, BigDecimal.valueOf(250), null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-EXPENSIVE");
    }

    @Test
    void withFilters_whenMaxPriceProvided_shouldFilterByMaxPrice() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, null, null, BigDecimal.valueOf(150), null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-ACTIVE");
    }

    @Test
    void withFilters_whenIsFeaturedTrue_shouldReturnFeaturedProducts() {
        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, null, null, null, null, true, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).isEmpty(); // none are featured
    }

    @Test
    void withFilters_whenTagSlugProvided_shouldFilterByTag() {
        Product tagged = productRepository.save(Product.builder()
                .sku("SKU-TAGGED").name("Tagged Product").slug("tagged-product")
                .basePrice(BigDecimal.valueOf(50))
                .category(category).brand(brand)
                .tags(Set.of(tag))
                .isActive(true).build());

        Specification<Product> spec = ProductSpecification.withFilters(
                null, null, "new", null, null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-TAGGED");
    }

    @Test
    void withFilters_whenAllFiltersCombined_shouldReturnCorrectResults() {
        Specification<Product> spec = ProductSpecification.withFilters(
                Set.of(category.getId()), "test-brand", null, "active",
                null, null, null, true);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSku()).isEqualTo("SKU-ACTIVE");
    }

    @Test
    void withFilters_shouldApplyDistinct() {
        Specification<Product> spec = ProductSpecification.withFilters(
                Set.of(category.getId()), null, null, null,
                null, null, null, null);
        List<Product> result = productRepository.findAll(spec);
        assertThat(result).hasSize(1);
    }
}
