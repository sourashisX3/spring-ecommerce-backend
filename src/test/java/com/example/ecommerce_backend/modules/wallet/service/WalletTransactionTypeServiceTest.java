package com.example.ecommerce_backend.modules.wallet.service;

import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import com.example.ecommerce_backend.modules.wallet.exception.WalletTransactionTypeNotFoundException;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionTypeRepository;
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
class WalletTransactionTypeServiceTest {

    @Mock
    private WalletTransactionTypeRepository walletTransactionTypeRepository;

    @InjectMocks
    private WalletTransactionTypeService walletTransactionTypeService;

    private WalletTransactionType type;

    @BeforeEach
    void setUp() {
        type = WalletTransactionType.builder()
                .id(1L).uuid("type-uuid")
                .code("CREDIT").name("Credit")
                .isActive(true).build();
    }

    @Test
    void toggleStatus_shouldToggle() {
        when(walletTransactionTypeRepository.findByUuid("type-uuid")).thenReturn(Optional.of(type));

        boolean result = walletTransactionTypeService.toggleStatus("type-uuid", false);

        assertThat(result).isTrue();
        assertThat(type.isActive()).isFalse();
        verify(walletTransactionTypeRepository).save(type);
    }

    @Test
    void toggleStatus_whenAlreadyInDesiredState_shouldReturnFalse() {
        type.setActive(false);
        when(walletTransactionTypeRepository.findByUuid("type-uuid")).thenReturn(Optional.of(type));

        boolean result = walletTransactionTypeService.toggleStatus("type-uuid", false);

        assertThat(result).isFalse();
        verify(walletTransactionTypeRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(walletTransactionTypeRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletTransactionTypeService.toggleStatus("nonexistent", true))
                .isInstanceOf(WalletTransactionTypeNotFoundException.class);
    }
}
