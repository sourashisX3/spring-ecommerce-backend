package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.modules.shipping.entity.DeliveryStatus;
import com.example.ecommerce_backend.modules.shipping.exception.DeliveryStatusNotFoundException;
import com.example.ecommerce_backend.modules.shipping.repository.DeliveryStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryStatusServiceTest {

    @Mock
    private DeliveryStatusRepository deliveryStatusRepository;

    @InjectMocks
    private DeliveryStatusService deliveryStatusService;

    private DeliveryStatus status;

    @BeforeEach
    void setUp() {
        status = DeliveryStatus.builder()
                .id(1L).uuid("status-uuid")
                .code("SHIPPED").name("Shipped")
                .isActive(true).build();
    }

    @Test
    void toggleStatus_shouldToggle() {
        when(deliveryStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = deliveryStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isTrue();
        assertThat(status.isActive()).isFalse();
        verify(deliveryStatusRepository).save(status);
    }

    @Test
    void toggleStatus_whenAlreadyInDesiredState_shouldReturnFalse() {
        status.setActive(false);
        when(deliveryStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = deliveryStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isFalse();
        verify(deliveryStatusRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(deliveryStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryStatusService.toggleStatus("nonexistent", true))
                .isInstanceOf(DeliveryStatusNotFoundException.class);
    }
}
