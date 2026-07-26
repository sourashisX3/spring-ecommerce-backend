package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.modules.payment.entity.RefundStatus;
import com.example.ecommerce_backend.modules.payment.exception.RefundStatusNotFoundException;
import com.example.ecommerce_backend.modules.payment.repository.RefundStatusRepository;
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
class RefundStatusServiceTest {

    @Mock
    private RefundStatusRepository refundStatusRepository;

    @InjectMocks
    private RefundStatusService refundStatusService;

    private RefundStatus status;

    @BeforeEach
    void setUp() {
        status = RefundStatus.builder()
                .id(1L).uuid("status-uuid")
                .code("PENDING").name("Pending")
                .isActive(true).build();
    }

    @Test
    void toggleStatus_shouldToggle() {
        when(refundStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = refundStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isTrue();
        assertThat(status.isActive()).isFalse();
        verify(refundStatusRepository).save(status);
    }

    @Test
    void toggleStatus_whenAlreadyInDesiredState_shouldReturnFalse() {
        status.setActive(false);
        when(refundStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = refundStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isFalse();
        verify(refundStatusRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(refundStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refundStatusService.toggleStatus("nonexistent", true))
                .isInstanceOf(RefundStatusNotFoundException.class);
    }
}
