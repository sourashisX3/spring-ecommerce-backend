package com.example.ecommerce_backend.modules.wallet.service;

import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyNotFoundException;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletTransactionResponse;
import com.example.ecommerce_backend.modules.wallet.entity.Wallet;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransaction;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import com.example.ecommerce_backend.modules.wallet.exception.InsufficientBalanceException;
import com.example.ecommerce_backend.modules.wallet.exception.WalletNotFoundException;
import com.example.ecommerce_backend.modules.wallet.exception.WalletTransactionTypeNotFoundException;
import com.example.ecommerce_backend.modules.wallet.repository.WalletRepository;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionRepository;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @Mock
    private WalletTransactionTypeRepository walletTransactionTypeRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletService walletService;

    private User user;
    private Wallet wallet;
    private WalletTransactionType creditType;
    private WalletTransactionType debitType;
    private Currency currency;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).uuid("user-uuid").email("test@test.com").build();
        currency = Currency.builder().id(1L).code("USD").build();
        wallet = Wallet.builder()
                .id(1L).uuid("wallet-uuid")
                .user(user)
                .balance(BigDecimal.valueOf(100))
                .currency(currency)
                .isActive(true)
                .build();
        creditType = WalletTransactionType.builder().id(1L).code("CREDIT").name("Credit").build();
        debitType = WalletTransactionType.builder().id(2L).code("DEBIT").name("Debit").build();
    }

    // --- getWallet ---

    @Test
    void getWallet_whenExists_shouldReturnWallet() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        WalletResponse result = walletService.getWallet(1L);

        assertThat(result.getUuid()).isEqualTo("wallet-uuid");
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void getWallet_whenNotExists_shouldCreateWallet() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse result = walletService.getWallet(1L);

        assertThat(result.getUuid()).isEqualTo("wallet-uuid");
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void getWallet_whenCreateFailsDueToConcurrency_shouldFindExisting() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(walletRepository.save(any(Wallet.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(wallet));

        WalletResponse result = walletService.getWallet(1L);

        assertThat(result.getUuid()).isEqualTo("wallet-uuid");
    }

    @Test
    void getWallet_whenCreateConcurrencyFails_shouldThrow() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(walletRepository.save(any(Wallet.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getWallet(1L))
                .isInstanceOf(WalletNotFoundException.class);
    }

    @Test
    void getWallet_whenCurrencyNotFound_shouldThrow() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getWallet(1L))
                .isInstanceOf(CurrencyNotFoundException.class);
    }

    // --- getTransactions (without pagination) ---

    @Test
    void getTransactions_withoutPagination_shouldReturnList() {
        WalletTransaction txn = WalletTransaction.builder()
                .id(1L).uuid("txn-uuid")
                .type(creditType)
                .amount(BigDecimal.TEN)
                .balanceBefore(BigDecimal.ZERO)
                .balanceAfter(BigDecimal.TEN)
                .build();

        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        when(walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(1L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(txn)));

        List<WalletTransactionResponse> result = walletService.getTransactions(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("txn-uuid");
    }

    @Test
    void getTransactions_withoutPagination_whenNoWallet_shouldThrow() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getTransactions(1L))
                .isInstanceOf(WalletNotFoundException.class);
    }

    // --- getTransactions (with pagination) ---

    @Test
    void getTransactions_withPagination_shouldReturnPage() {
        WalletTransaction txn = WalletTransaction.builder()
                .id(1L).uuid("txn-uuid")
                .type(creditType)
                .amount(BigDecimal.TEN)
                .balanceBefore(BigDecimal.ZERO)
                .balanceAfter(BigDecimal.TEN)
                .build();

        Page<WalletTransaction> page = new PageImpl<>(List.of(txn));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        when(walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<WalletTransactionResponse> result = walletService.getTransactions(1L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo("txn-uuid");
    }

    @Test
    void getTransactions_withPagination_whenNoWallet_shouldThrow() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getTransactions(1L, PageRequest.of(0, 10)))
                .isInstanceOf(WalletNotFoundException.class);
    }

    // --- credit ---

    @Test
    void credit_shouldAddBalanceAndCreateTransaction() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(walletTransactionTypeRepository.findByCode("CREDIT")).thenReturn(Optional.of(creditType));

        WalletResponse result = walletService.credit(1L, BigDecimal.valueOf(50), "order", 1L, "Refund");

        assertThat(result.getUuid()).isEqualTo("wallet-uuid");
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
        verify(walletTransactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void credit_withZeroAmount_shouldThrow() {
        assertThatThrownBy(() -> walletService.credit(1L, BigDecimal.ZERO, "order", 1L, "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void credit_withNegativeAmount_shouldThrow() {
        assertThatThrownBy(() -> walletService.credit(1L, BigDecimal.valueOf(-10), "order", 1L, "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void credit_whenWalletNotFound_shouldThrow() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.credit(1L, BigDecimal.TEN, "order", 1L, "test"))
                .isInstanceOf(WalletNotFoundException.class);
    }

    @Test
    void credit_whenCreditTypeNotFound_shouldThrow() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(walletTransactionTypeRepository.findByCode("CREDIT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.credit(1L, BigDecimal.TEN, "order", 1L, "test"))
                .isInstanceOf(WalletTransactionTypeNotFoundException.class);
    }

    // --- debit ---

    @Test
    void debit_shouldSubtractBalanceAndCreateTransaction() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(walletTransactionTypeRepository.findByCode("DEBIT")).thenReturn(Optional.of(debitType));

        WalletResponse result = walletService.debit(1L, BigDecimal.valueOf(30), "order", 1L, "Purchase");

        assertThat(result.getUuid()).isEqualTo("wallet-uuid");
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(70));
        verify(walletTransactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void debit_withInsufficientBalance_shouldThrow() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> walletService.debit(1L, BigDecimal.valueOf(200), "order", 1L, "test"))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void debit_withZeroAmount_shouldThrow() {
        assertThatThrownBy(() -> walletService.debit(1L, BigDecimal.ZERO, "order", 1L, "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void debit_withNegativeAmount_shouldThrow() {
        assertThatThrownBy(() -> walletService.debit(1L, BigDecimal.valueOf(-10), "order", 1L, "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void debit_whenWalletNotFound_shouldThrow() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.debit(1L, BigDecimal.TEN, "order", 1L, "test"))
                .isInstanceOf(WalletNotFoundException.class);
    }

    @Test
    void debit_whenDebitTypeNotFound_shouldThrow() {
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(walletTransactionTypeRepository.findByCode("DEBIT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.debit(1L, BigDecimal.TEN, "order", 1L, "test"))
                .isInstanceOf(WalletTransactionTypeNotFoundException.class);
    }
}
