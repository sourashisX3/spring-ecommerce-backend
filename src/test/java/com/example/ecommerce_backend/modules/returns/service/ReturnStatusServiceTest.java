package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.modules.returns.entity.ReturnStatus;
import com.example.ecommerce_backend.modules.returns.exception.ReturnStatusNotFoundException;
import com.example.ecommerce_backend.modules.returns.repository.ReturnStatusRepository;
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
class ReturnStatusServiceTest {

    @Mock
    private ReturnStatusRepository returnStatusRepository;

    @InjectMocks
    private ReturnStatusService returnStatusService;

    private ReturnStatus status;

    @BeforeEach
    void setUp() {
        status = ReturnStatus.builder()
                .id(1L).uuid("status-uuid")
                .code("APPROVED").name("Approved")
                .isActive(true).build();
    }

    @Test
    void toggleStatus_shouldToggle() {
        when(returnStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = returnStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isTrue();
        assertThat(status.isActive()).isFalse();
        verify(returnStatusRepository).save(status);
    }

    @Test
    void toggleStatus_whenAlreadyInDesiredState_shouldReturnFalse() {
        status.setActive(false);
        when(returnStatusRepository.findByUuid("status-uuid")).thenReturn(Optional.of(status));

        boolean result = returnStatusService.toggleStatus("status-uuid", false);

        assertThat(result).isFalse();
        verify(returnStatusRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(returnStatusRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> returnStatusService.toggleStatus("nonexistent", true))
                .isInstanceOf(ReturnStatusNotFoundException.class);
    }
}
