package com.example.ecommerce_backend.modules.currency.service;

import com.example.ecommerce_backend.modules.currency.dto.request.CurrencyRequest;
import com.example.ecommerce_backend.modules.currency.dto.request.CurrencyUpdateRequest;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyAlreadyExistsException;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyDefaultException;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyNotFoundException;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Transactional(readOnly = true)
    public List<Currency> getAllCurrencies(Boolean active, String location) {
        return currencyRepository.findAll().stream()
                .filter(c -> active == null || c.isActive() == active)
                .filter(c -> location == null || location.isBlank()
                        || (c.getLocation() != null && c.getLocation().equalsIgnoreCase(location.trim())))
                .sorted(java.util.Comparator
                        .comparingInt(Currency::getSortOrder)
                        .thenComparing(Currency::getId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Currency getByUuid(String uuid) {
        return currencyRepository.findByUuid(uuid)
                .orElseThrow(() -> new CurrencyNotFoundException(uuid));
    }

    @Transactional(readOnly = true)
    public Currency getDefault() {
        return currencyRepository.findByIsDefaultTrueAndIsActiveTrue()
                .or(() -> currencyRepository.findFirstByIsActiveTrueOrderBySortOrderAscIdAsc())
                .orElseThrow(() -> new CurrencyNotFoundException("No active default currency"));
    }

    @Transactional(readOnly = true)
    public Currency getByCode(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException(code));
    }

    @Transactional
    public Currency createCurrency(CurrencyRequest request) {
        if (currencyRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new CurrencyAlreadyExistsException(request.getCode());
        }
        boolean makeDefault = Boolean.TRUE.equals(request.getIsDefault());
        if (makeDefault) {
            clearDefault();
        }
        Currency currency = Currency.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .symbol(request.getSymbol())
                .sortOrder(request.getSortOrder())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .location(request.getLocation() != null ? request.getLocation().trim() : null)
                .isDefault(makeDefault)
                .exchangeRate(request.getExchangeRate() != null && request.getExchangeRate().compareTo(BigDecimal.ZERO) > 0
                        ? request.getExchangeRate()
                        : BigDecimal.ONE)
                .build();
        return currencyRepository.save(currency);
    }

    @Transactional
    public Currency updateCurrency(String uuid, CurrencyUpdateRequest request) {
        Currency currency = currencyRepository.findByUuid(uuid)
                .orElseThrow(() -> new CurrencyNotFoundException(uuid));
        if (request.getName() != null && !request.getName().isBlank()) {
            currency.setName(request.getName().trim());
        }
        if (request.getSymbol() != null) {
            currency.setSymbol(request.getSymbol());
        }
        if (request.getLocation() != null) {
            currency.setLocation(request.getLocation().trim());
        }
        if (request.getIsActive() != null) {
            currency.setActive(request.getIsActive());
        }
        if (request.getSortOrder() != null) {
            currency.setSortOrder(request.getSortOrder());
        }
        if (request.getExchangeRate() != null && request.getExchangeRate().compareTo(BigDecimal.ZERO) > 0) {
            currency.setExchangeRate(request.getExchangeRate());
        }
        return currencyRepository.save(currency);
    }

    @Transactional
    public int refreshRates(Map<String, BigDecimal> rates) {
        if (rates == null || rates.isEmpty()) {
            return 0;
        }
        int updated = 0;
        for (Currency currency : currencyRepository.findByIsActiveTrue()) {
            BigDecimal rate = rates.get(currency.getCode().toUpperCase());
            if (rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
                currency.setExchangeRate(rate);
                updated++;
            }
        }
        currencyRepository.flush();
        return updated;
    }

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        Currency currency = currencyRepository.findByUuid(uuid)
                .orElseThrow(() -> new CurrencyNotFoundException(uuid));
        if (currency.isActive() == isActive) {
            return false;
        }
        if (currency.isDefault() && !isActive) {
            throw new CurrencyDefaultException(
                    "Cannot deactivate the default currency. Set another currency as default first.");
        }
        currency.setActive(isActive);
        currencyRepository.save(currency);
        return true;
    }

    @Transactional
    public Currency makeDefault(String uuid) {
        Currency currency = currencyRepository.findByUuid(uuid)
                .orElseThrow(() -> new CurrencyNotFoundException(uuid));
        clearDefault();
        currency.setDefault(true);
        currency.setActive(true);
        return currencyRepository.save(currency);
    }

    private void clearDefault() {
        currencyRepository.findAll().forEach(c -> c.setDefault(false));
        currencyRepository.flush();
    }
}
