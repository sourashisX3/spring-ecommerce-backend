package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.modules.shipping.dto.request.ShippingCarrierRequest;
import com.example.ecommerce_backend.modules.shipping.exception.ShippingCarrierConflictException;
import com.example.ecommerce_backend.modules.shipping.exception.ShippingCarrierNotFoundException;
import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingCarrier;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingCarrierRepository;
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
class ShippingCarrierServiceTest {

    @Mock
    private ShippingCarrierRepository shippingCarrierRepository;

    @InjectMocks
    private ShippingCarrierService shippingCarrierService;

    private ShippingCarrier carrier;

    @BeforeEach
    void setUp() {
        carrier = ShippingCarrier.builder()
                .id(1L).uuid("carrier-uuid")
                .code("UPS")
                .name("UPS")
                .trackingUrlTemplate("https://ups.com/track/{tracking}")
                .isActive(true)
                .build();
    }

    // --- getAll ---

    @Test
    void getAll_shouldReturnAllCarriers() {
        when(shippingCarrierRepository.findAll()).thenReturn(List.of(carrier));

        List<ShippingCarrierResponse> result = shippingCarrierService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("UPS");
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {
        when(shippingCarrierRepository.findAll()).thenReturn(List.of());

        List<ShippingCarrierResponse> result = shippingCarrierService.getAll();

        assertThat(result).isEmpty();
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnCarrier() {
        when(shippingCarrierRepository.findByUuid("carrier-uuid")).thenReturn(Optional.of(carrier));

        ShippingCarrierResponse result = shippingCarrierService.getByUuid("carrier-uuid");

        assertThat(result.getCode()).isEqualTo("UPS");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(shippingCarrierRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingCarrierService.getByUuid("nonexistent"))
                .isInstanceOf(ShippingCarrierNotFoundException.class);
    }

    // --- create ---

    @Test
    void create_shouldCreateCarrier() {
        ShippingCarrierRequest request = new ShippingCarrierRequest();
        request.setCode("FEDEX");
        request.setName("FedEx");
        request.setTrackingUrlTemplate("https://fedex.com/track/{tracking}");

        when(shippingCarrierRepository.findByCode("FEDEX")).thenReturn(Optional.empty());
        when(shippingCarrierRepository.save(any(ShippingCarrier.class))).thenReturn(carrier);

        ShippingCarrierResponse result = shippingCarrierService.create(request);

        assertThat(result.getCode()).isEqualTo("UPS");
    }

    @Test
    void create_whenCodeExists_shouldThrow() {
        ShippingCarrierRequest request = new ShippingCarrierRequest();
        request.setCode("UPS");
        request.setName("UPS");

        when(shippingCarrierRepository.findByCode("UPS")).thenReturn(Optional.of(carrier));

        assertThatThrownBy(() -> shippingCarrierService.create(request))
                .isInstanceOf(ShippingCarrierConflictException.class);
    }

    // --- update ---

    @Test
    void update_shouldUpdateCarrier() {
        ShippingCarrierRequest request = new ShippingCarrierRequest();
        request.setCode("UPS");
        request.setName("UPS Updated");
        request.setTrackingUrlTemplate("https://ups.com/track/new");

        when(shippingCarrierRepository.findByUuid("carrier-uuid")).thenReturn(Optional.of(carrier));
        when(shippingCarrierRepository.save(any(ShippingCarrier.class))).thenReturn(carrier);

        ShippingCarrierResponse result = shippingCarrierService.update("carrier-uuid", request);

        assertThat(result.getName()).isEqualTo("UPS Updated");
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        ShippingCarrierRequest request = new ShippingCarrierRequest();
        request.setCode("UPS");
        request.setName("UPS");

        when(shippingCarrierRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingCarrierService.update("nonexistent", request))
                .isInstanceOf(ShippingCarrierNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_shouldToggleActive() {
        when(shippingCarrierRepository.findByUuid("carrier-uuid")).thenReturn(Optional.of(carrier));
        when(shippingCarrierRepository.save(any(ShippingCarrier.class))).thenReturn(carrier);

        ShippingCarrierResponse result = shippingCarrierService.toggleStatus("carrier-uuid");

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(shippingCarrierRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingCarrierService.toggleStatus("nonexistent"))
                .isInstanceOf(ShippingCarrierNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteCarrier() {
        when(shippingCarrierRepository.findByUuid("carrier-uuid")).thenReturn(Optional.of(carrier));

        shippingCarrierService.delete("carrier-uuid");

        verify(shippingCarrierRepository).delete(carrier);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(shippingCarrierRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingCarrierService.delete("nonexistent"))
                .isInstanceOf(ShippingCarrierNotFoundException.class);
    }
}
