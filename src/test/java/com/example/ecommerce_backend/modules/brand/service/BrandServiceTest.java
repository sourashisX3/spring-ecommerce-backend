package com.example.ecommerce_backend.modules.brand.service;

import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.entity.Brand;
import com.example.ecommerce_backend.modules.brand.exception.BrandNotFoundException;
import com.example.ecommerce_backend.modules.brand.repository.BrandRepository;
import com.example.ecommerce_backend.modules.brand.service.BrandService;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand activeBrand;
    private Brand inactiveBrand;

    @BeforeEach
    void setUp() {
        activeBrand = Brand.builder().id(1L).uuid("uuid-1").name("Active").slug("active").isActive(true).build();
        inactiveBrand = Brand.builder().id(2L).uuid("uuid-2").name("Inactive").slug("inactive").isActive(false).build();
    }

    @Test
    void getAll_whenActiveNull_shouldReturnAll() {
        when(brandRepository.findAll()).thenReturn(List.of(activeBrand, inactiveBrand));
        when(productRepository.countByBrandId(anyLong())).thenReturn(0L);

        List<BrandResponse> result = brandService.getAll(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAll_whenActiveTrue_shouldReturnOnlyActive() {
        when(brandRepository.findAll()).thenReturn(List.of(activeBrand, inactiveBrand));
        when(productRepository.countByBrandId(anyLong())).thenReturn(0L);

        List<BrandResponse> result = brandService.getAll(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    void getAll_whenActiveFalse_shouldReturnOnlyInactive() {
        when(brandRepository.findAll()).thenReturn(List.of(activeBrand, inactiveBrand));
        when(productRepository.countByBrandId(anyLong())).thenReturn(0L);

        List<BrandResponse> result = brandService.getAll(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isFalse();
    }

    @Test
    void getAll_shouldSetProductCount() {
        when(brandRepository.findAll()).thenReturn(List.of(activeBrand));
        when(productRepository.countByBrandId(1L)).thenReturn(10L);

        List<BrandResponse> result = brandService.getAll(null);

        assertThat(result.get(0).getProductCount()).isEqualTo(10);
    }

    @Test
    void getBySlug_shouldReturnBrand() {
        when(brandRepository.findBySlug("active")).thenReturn(Optional.of(activeBrand));
        when(productRepository.countByBrandId(1L)).thenReturn(5L);

        BrandResponse result = brandService.getBySlug("active");

        assertThat(result.getSlug()).isEqualTo("active");
        assertThat(result.getProductCount()).isEqualTo(5);
    }

    @Test
    void getBySlug_whenNotFound_shouldThrow() {
        when(brandRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.getBySlug("nonexistent"))
                .isInstanceOf(BrandNotFoundException.class);
    }

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnFalse() {
        when(brandRepository.findByUuid("uuid-1")).thenReturn(Optional.of(activeBrand));

        boolean changed = brandService.toggleStatus("uuid-1", true);

        assertThat(changed).isFalse();
        verify(brandRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenAlreadyInactive_shouldReturnFalse() {
        when(brandRepository.findByUuid("uuid-2")).thenReturn(Optional.of(inactiveBrand));

        boolean changed = brandService.toggleStatus("uuid-2", false);

        assertThat(changed).isFalse();
        verify(brandRepository, never()).save(any());
    }

    @Test
    void toggleStatus_shouldToggleActiveToInactive() {
        when(brandRepository.findByUuid("uuid-1")).thenReturn(Optional.of(activeBrand));

        boolean changed = brandService.toggleStatus("uuid-1", false);

        assertThat(changed).isTrue();
        assertThat(activeBrand.isActive()).isFalse();
        verify(brandRepository).save(activeBrand);
    }

    @Test
    void toggleStatus_shouldToggleInactiveToActive() {
        when(brandRepository.findByUuid("uuid-2")).thenReturn(Optional.of(inactiveBrand));

        boolean changed = brandService.toggleStatus("uuid-2", true);

        assertThat(changed).isTrue();
        assertThat(inactiveBrand.isActive()).isTrue();
        verify(brandRepository).save(inactiveBrand);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(brandRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.toggleStatus("nonexistent", true))
                .isInstanceOf(BrandNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteBrand() {
        when(brandRepository.findByUuid("uuid-1")).thenReturn(Optional.of(activeBrand));

        brandService.delete("uuid-1");

        verify(brandRepository).delete(activeBrand);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(brandRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.delete("nonexistent"))
                .isInstanceOf(BrandNotFoundException.class);
    }
}