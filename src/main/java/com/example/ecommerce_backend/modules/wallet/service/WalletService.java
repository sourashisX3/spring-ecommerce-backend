package com.example.ecommerce_backend.modules.wallet.service;

import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletTransactionResponse;
import com.example.ecommerce_backend.modules.wallet.entity.Wallet;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransaction;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import com.example.ecommerce_backend.modules.wallet.exception.InsufficientBalanceException;
import com.example.ecommerce_backend.modules.wallet.exception.WalletNotFoundException;
import com.example.ecommerce_backend.modules.wallet.mapper.WalletMapper;
import com.example.ecommerce_backend.modules.wallet.mapper.WalletTransactionMapper;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.wallet.repository.WalletRepository;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionRepository;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private WalletTransactionTypeRepository walletTransactionTypeRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public WalletResponse getWallet(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));
        return WalletMapper.toResponse(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getTransactions(Long userId) {
        return getTransactions(userId, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    public Page<WalletTransactionResponse> getTransactions(Long userId, Pageable pageable) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable)
                .map(WalletTransactionMapper::toResponse);
    }

    @Transactional
    public WalletResponse credit(Long userId, BigDecimal amount, String referenceType, Long referenceId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        wallet.setBalance(balanceAfter);
        wallet = walletRepository.save(wallet);

        WalletTransactionType creditType = walletTransactionTypeRepository.findByCode("CREDIT")
                .orElseThrow(() -> new RuntimeException("WalletTransactionType not found: CREDIT"));

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .type(creditType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .description(description)
                .build();
        walletTransactionRepository.save(transaction);

        return WalletMapper.toResponse(wallet);
    }

    @Transactional
    public WalletResponse debit(Long userId, BigDecimal amount, String referenceType, Long referenceId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        BigDecimal balanceBefore = wallet.getBalance();
        if (balanceBefore.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        wallet.setBalance(balanceAfter);
        wallet = walletRepository.save(wallet);

        WalletTransactionType debitType = walletTransactionTypeRepository.findByCode("DEBIT")
                .orElseThrow(() -> new RuntimeException("WalletTransactionType not found: DEBIT"));

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .type(debitType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .description(description)
                .build();
        walletTransactionRepository.save(transaction);

        return WalletMapper.toResponse(wallet);
    }

    private Wallet createWallet(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            Currency currency = currencyRepository.findByCode("USD")
                    .orElseThrow(() -> new RuntimeException("Currency not found: USD"));
            Wallet wallet = Wallet.builder()
                    .user(user)
                    .currency(currency)
                    .build();
            return walletRepository.save(wallet);
        } catch (DataIntegrityViolationException e) {
            return walletRepository.findByUserIdWithLock(userId)
                    .orElseThrow(() -> new WalletNotFoundException("Unable to create wallet"));
        }
    }
}
