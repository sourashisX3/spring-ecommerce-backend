package com.example.ecommerce_backend.modules.currency.service;

import com.example.ecommerce_backend.modules.currency.dto.request.CurrencyRequest;
import com.example.ecommerce_backend.modules.currency.dto.request.CurrencyUpdateRequest;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyNotFoundException;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyService currencyService;

    private Currency usd;
    private Currency eur;

    @BeforeEach
    void setUp() {
        usd = Currency.builder()
                .id(1L).uuid("uuid-usd").code("USD").name("US Dollar")
                .symbol("$").sortOrder(1).isActive(true)
                .build();

        eur = Currency.builder()
                .id(2L).uuid("uuid-eur").code("EUR").name("Euro")
                .symbol("\u20AC").sortOrder(2).isActive(true)
                .build();
    }

    @Test
    void getAllCurrencies_shouldReturnAll() {
        when(currencyRepository.findAll()).thenReturn(List.of(usd, eur));

        List<Currency> result = currencyService.getAllCurrencies(null, null);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Currency::getCode).containsExactly("USD", "EUR");
    }

    @Test
    void getByUuid_shouldReturnCurrency() {
        when(currencyRepository.findByUuid("uuid-usd")).thenReturn(Optional.of(usd));

        Currency result = currencyService.getByUuid("uuid-usd");

        assertThat(result.getCode()).isEqualTo("USD");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(currencyRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.getByUuid("nonexistent"))
                .isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    void getByCode_shouldReturnCurrency() {
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(eur));

        Currency result = currencyService.getByCode("EUR");

        assertThat(result.getUuid()).isEqualTo("uuid-eur");
    }

    @Test
    void getByCode_whenNotFound_shouldThrow() {
        when(currencyRepository.findByCode("XYZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.getByCode("XYZ"))
                .isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    void createCurrency_shouldPersistExchangeRate() {
        when(currencyRepository.existsByCode("USD")).thenReturn(false);
        when(currencyRepository.save(any(Currency.class))).thenAnswer(inv -> inv.getArgument(0));

        CurrencyRequest request = new CurrencyRequest();
        request.setCode("USD");
        request.setName("US Dollar");
        request.setIsActive(true);
        request.setExchangeRate(new BigDecimal("85.000000"));

        Currency result = currencyService.createCurrency(request);

        assertThat(result.getExchangeRate()).isEqualByComparingTo("85.000000");
    }

    @Test
    void createCurrency_shouldDefaultExchangeRateToOneWhenMissing() {
        when(currencyRepository.existsByCode("USD")).thenReturn(false);
        when(currencyRepository.save(any(Currency.class))).thenAnswer(inv -> inv.getArgument(0));

        CurrencyRequest request = new CurrencyRequest();
        request.setCode("USD");
        request.setName("US Dollar");
        request.setIsActive(true);

        Currency result = currencyService.createCurrency(request);

        assertThat(result.getExchangeRate()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void updateCurrency_shouldUpdateExchangeRate() {
        when(currencyRepository.findByUuid("uuid-usd")).thenReturn(Optional.of(usd));
        when(currencyRepository.save(any(Currency.class))).thenAnswer(inv -> inv.getArgument(0));

        CurrencyUpdateRequest request = new CurrencyUpdateRequest();
        request.setName("US Dollar");
        request.setExchangeRate(new BigDecimal("90.000000"));

        Currency result = currencyService.updateCurrency("uuid-usd", request);

        assertThat(result.getExchangeRate()).isEqualByComparingTo("90.000000");
        assertThat(result.getCode()).isEqualTo("USD");
    }

    @Test
    void updateCurrency_whenInvalidRate_shouldKeepExisting() {
        when(currencyRepository.findByUuid("uuid-usd")).thenReturn(Optional.of(usd));
        when(currencyRepository.save(any(Currency.class))).thenAnswer(inv -> inv.getArgument(0));

        CurrencyUpdateRequest request = new CurrencyUpdateRequest();
        request.setName("US Dollar");
        request.setExchangeRate(new BigDecimal("-5"));

        Currency result = currencyService.updateCurrency("uuid-usd", request);

        assertThat(result.getExchangeRate()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void refreshRates_shouldUpdateActiveCurrencies() {
        usd.setExchangeRate(BigDecimal.ONE);
        when(currencyRepository.findByIsActiveTrue()).thenReturn(List.of(usd, eur));

        Map<String, BigDecimal> rates = Map.of(
                "USD", new BigDecimal("85.000000"),
                "INR", BigDecimal.ONE);

        int updated = currencyService.refreshRates(rates);

        assertThat(updated).isEqualTo(1);
        assertThat(usd.getExchangeRate()).isEqualByComparingTo("85.000000");
        // No matching rate for EUR -> unchanged
        assertThat(eur.getExchangeRate()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void refreshRates_withEmptyMap_shouldUpdateNothing() {
        int updated = currencyService.refreshRates(Map.of());

        assertThat(updated).isZero();
    }
}
