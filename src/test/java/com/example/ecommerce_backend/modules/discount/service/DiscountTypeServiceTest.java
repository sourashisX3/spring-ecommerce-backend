package com.example.ecommerce_backend.modules.discount.service;

import com.example.ecommerce_backend.modules.discount.dto.request.DiscountTypeRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountTypeServiceTest {

    @Mock
    private DiscountTypeRepository discountTypeRepository;

    @InjectMocks
    private DiscountTypeService discountTypeService;

    private DiscountType percentageType;
    private DiscountType fixedType;

    @BeforeEach
    void setUp() {
        percentageType = DiscountType.builder()
                .id(1L).uuid("dt-uuid-1").code("PERCENTAGE").name("Percentage")
                .description("Percentage discount").computation("PERCENTAGE")
                .configSchema("{}").isActive(true).build();

        fixedType = DiscountType.builder()
                .id(2L).uuid("dt-uuid-2").code("FIXED").name("Fixed Amount")
                .description("Fixed amount discount").computation("FIXED")
                .configSchema("{}").isActive(true).build();
    }

    // --- create ---

    @Test
    void create_shouldSaveAndReturnDiscountType() {
        DiscountTypeRequest request = new DiscountTypeRequest();
        request.setCode("NEW_TYPE");
        request.setName("New Type");
        request.setDescription("New discount type");
        request.setComputation("PERCENTAGE");
        request.setConfigSchema("{}");

        when(discountTypeRepository.save(any(DiscountType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiscountTypeResponse result = discountTypeService.create(request);

        assertThat(result.getCode()).isEqualTo("NEW_TYPE");
        assertThat(result.getName()).isEqualTo("New Type");
        verify(discountTypeRepository).save(any(DiscountType.class));
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnDiscountType() {
        when(discountTypeRepository.findByUuid("dt-uuid-1")).thenReturn(Optional.of(percentageType));

        DiscountTypeResponse result = discountTypeService.getByUuid("dt-uuid-1");

        assertThat(result.getCode()).isEqualTo("PERCENTAGE");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(discountTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountTypeService.getByUuid("nonexistent"))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }

    // --- getByCode ---

    @Test
    void getByCode_shouldReturnDiscountType() {
        when(discountTypeRepository.findByCode("PERCENTAGE")).thenReturn(Optional.of(percentageType));

        DiscountTypeResponse result = discountTypeService.getByCode("PERCENTAGE");

        assertThat(result.getUuid()).isEqualTo("dt-uuid-1");
    }

    @Test
    void getByCode_whenNotFound_shouldThrow() {
        when(discountTypeRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountTypeService.getByCode("INVALID"))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }

    // --- getAll ---

    @Test
    void getAll_shouldReturnAllDiscountTypes() {
        when(discountTypeRepository.findAll()).thenReturn(List.of(percentageType, fixedType));

        List<DiscountTypeResponse> result = discountTypeService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DiscountTypeResponse::getCode)
                .containsExactlyInAnyOrder("PERCENTAGE", "FIXED");
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {
        when(discountTypeRepository.findAll()).thenReturn(List.of());

        List<DiscountTypeResponse> result = discountTypeService.getAll();

        assertThat(result).isEmpty();
    }

    // --- update ---

    @Test
    void update_shouldUpdateAndReturnDiscountType() {
        DiscountTypeRequest request = new DiscountTypeRequest();
        request.setCode("UPDATED");
        request.setName("Updated Type");
        request.setDescription("Updated");
        request.setComputation("FIXED");
        request.setConfigSchema("{\"key\": \"value\"}");

        when(discountTypeRepository.findByUuid("dt-uuid-1")).thenReturn(Optional.of(percentageType));
        when(discountTypeRepository.save(any(DiscountType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DiscountTypeResponse result = discountTypeService.update("dt-uuid-1", request);

        assertThat(result.getCode()).isEqualTo("UPDATED");
        assertThat(result.getName()).isEqualTo("Updated Type");
        assertThat(result.getComputation()).isEqualTo("FIXED");
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        when(discountTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountTypeService.update("nonexistent", new DiscountTypeRequest()))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_shouldToggleActive() {
        when(discountTypeRepository.findByUuid("dt-uuid-1")).thenReturn(Optional.of(percentageType));

        discountTypeService.toggleStatus("dt-uuid-1", false);

        assertThat(percentageType.isActive()).isFalse();
        verify(discountTypeRepository).save(percentageType);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(discountTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountTypeService.toggleStatus("nonexistent", true))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteDiscountType() {
        when(discountTypeRepository.findByUuid("dt-uuid-1")).thenReturn(Optional.of(percentageType));

        discountTypeService.delete("dt-uuid-1");

        verify(discountTypeRepository).delete(percentageType);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(discountTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountTypeService.delete("nonexistent"))
                .isInstanceOf(DiscountTypeNotFoundException.class);
    }
}
