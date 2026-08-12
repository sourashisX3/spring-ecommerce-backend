package com.example.ecommerce_backend.modules.wallet.service;

import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletTransactionResponse;
import com.example.ecommerce_backend.modules.wallet.entity.Wallet;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransaction;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import com.example.ecommerce_backend.modules.wallet.exception.InsufficientBalanceException;
import com.example.ecommerce_backend.modules.wallet.exception.InvalidAmountException;
import com.example.ecommerce_backend.modules.wallet.exception.WalletInactiveException;
import com.example.ecommerce_backend.modules.wallet.exception.WalletNotFoundException;
import com.example.ecommerce_backend.modules.wallet.exception.WalletTransactionTypeNotFoundException;
import com.example.ecommerce_backend.modules.wallet.mapper.WalletMapper;
import com.example.ecommerce_backend.modules.wallet.mapper.WalletTransactionMapper;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyNotFoundException;
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

    @Transactional(readOnly = true)
    public Page<WalletResponse> listAllWallets(String search, Pageable pageable) {
        Page<Wallet> wallets;
        if (search != null && !search.isBlank()) {
            String q = search.trim();
            wallets = walletRepository
                    .findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
                            q, q, q, pageable);
        } else {
            wallets = walletRepository.findAll(pageable);
        }
        return wallets.map(WalletMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WalletTransactionResponse> getWalletTransactionsByWalletUuid(String uuid, Pageable pageable) {
        Wallet wallet = walletRepository.findByUuid(uuid)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for uuid: " + uuid));
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable)
                .map(WalletTransactionMapper::toResponse);
    }

    @Transactional
    public WalletResponse creditWallet(String uuid, BigDecimal amount, String description) {
        Wallet wallet = walletRepository.findByUuid(uuid)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for uuid: " + uuid));
        return credit(wallet.getUser().getId(), amount, "ADMIN_CREDIT", null, description);
    }

    @Transactional
    public WalletResponse debitWallet(String uuid, BigDecimal amount, String description) {
        Wallet wallet = walletRepository.findByUuid(uuid)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for uuid: " + uuid));
        return debit(wallet.getUser().getId(), amount, "ADMIN_DEBIT", null, description);
    }

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        Wallet wallet = walletRepository.findByUuid(uuid)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for uuid: " + uuid));
        if (wallet.isActive() == isActive) {
            return false;
        }
        wallet.setActive(isActive);
        walletRepository.save(wallet);
        return true;
    }

    @Transactional
    public WalletResponse credit(Long userId, BigDecimal amount, String referenceType, Long referenceId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Credit amount must be positive");
        }
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        if (!wallet.isActive()) {
            throw new WalletInactiveException("Wallet is deactivated. Credits are not allowed.");
        }

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        wallet.setBalance(balanceAfter);
        wallet = walletRepository.save(wallet);

        WalletTransactionType creditType = walletTransactionTypeRepository.findByCode("CREDIT")
                .orElseThrow(() -> new WalletTransactionTypeNotFoundException("CREDIT"));

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
            throw new InvalidAmountException("Debit amount must be positive");
        }
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        if (!wallet.isActive()) {
            throw new WalletInactiveException("Wallet is deactivated. Debits are not allowed.");
        }

        BigDecimal balanceBefore = wallet.getBalance();
        if (balanceBefore.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        wallet.setBalance(balanceAfter);
        wallet = walletRepository.save(wallet);

        WalletTransactionType debitType = walletTransactionTypeRepository.findByCode("DEBIT")
                .orElseThrow(() -> new WalletTransactionTypeNotFoundException("DEBIT"));

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
            Currency currency = currencyRepository.findByIsDefaultTrueAndIsActiveTrue()
                    .orElseGet(() -> currencyRepository.findFirstByIsActiveTrueOrderBySortOrderAscIdAsc()
                            .orElseThrow(() -> new CurrencyNotFoundException("No active default currency")));
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
