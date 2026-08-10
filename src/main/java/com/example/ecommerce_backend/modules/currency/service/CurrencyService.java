package com.example.ecommerce_backend.modules.currency.service;

import com.example.ecommerce_backend.modules.currency.dto.request.CurrencyRequest;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyAlreadyExistsException;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyNotFoundException;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Transactional(readOnly = true)
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Currency getByUuid(String uuid) {
        return currencyRepository.findByUuid(uuid)
                .orElseThrow(() -> new CurrencyNotFoundException(uuid));
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
        Currency currency = Currency.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .symbol(request.getSymbol())
                .sortOrder(request.getSortOrder())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .build();
        return currencyRepository.save(currency);
    }

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        Currency currency = currencyRepository.findByUuid(uuid)
                .orElseThrow(() -> new CurrencyNotFoundException(uuid));
        if (currency.isActive() == isActive) {
            return false;
        }
        currency.setActive(isActive);
        currencyRepository.save(currency);
        return true;
    }
}
