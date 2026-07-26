package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.modules.payment.entity.PaymentStatus;
import com.example.ecommerce_backend.modules.payment.exception.PaymentStatusNotFoundException;
import com.example.ecommerce_backend.modules.payment.repository.PaymentStatusRepository;
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
class PaymentStatusServiceTest {

    @Mock
    private PaymentStatusRepository paymentStatusRepository;

    @InjectMocks
    private PaymentStatusService paymentStatusService;

    private PaymentStatus status;

    @BeforeEach
    void setUp() {
        status = PaymentStatus.builder()
                .id(1L).uuid("status-uuid")
                .code("PENDING").name("Pending")
                .isActive(true).build();
    }

    @Test
    void toggleStatus_shouldToggle() {
        when(paymentStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = paymentStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isTrue();
        assertThat(status.isActive()).isFalse();
        verify(paymentStatusRepository).save(status);
    }

    @Test
    void toggleStatus_whenAlreadyInDesiredState_shouldReturnFalse() {
        status.setActive(false);
        when(paymentStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = paymentStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isFalse();
        verify(paymentStatusRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(paymentStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentStatusService.toggleStatus("nonexistent", true))
                .isInstanceOf(PaymentStatusNotFoundException.class);
    }
}
