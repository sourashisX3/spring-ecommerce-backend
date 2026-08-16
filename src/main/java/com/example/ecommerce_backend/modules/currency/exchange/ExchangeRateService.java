package com.example.ecommerce_backend.modules.currency.exchange;

import com.example.ecommerce_backend.modules.currency.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates fetching exchange rates from the configured provider and
 * persisting them onto currency records.
 */
@Service
public class ExchangeRateService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateService.class);

    private final List<ExchangeRateProvider> providers;
    private final CurrencyService currencyService;
    private final String providerName;
    private final String anchor;

    @Autowired
    public ExchangeRateService(
            List<ExchangeRateProvider> providers,
            CurrencyService currencyService,
            @Value("${app.currency.rate-provider:frankfurter}") String providerName,
            @Value("${app.currency.anchor:INR}") String anchor) {
        this.providers = providers;
        this.currencyService = currencyService;
        this.providerName = providerName;
        this.anchor = anchor;
    }

    /**
     * Fetches the latest rates from the active provider and stores them.
     *
     * @return number of currencies updated, or -1 when the fetch failed
     */
    public int refreshRates() {
        try {
            ExchangeRateProvider provider = providers.stream()
                    .filter(p -> p.name().equalsIgnoreCase(providerName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No exchange rate provider registered with name '" + providerName + "'"));

            Map<String, BigDecimal> rates = provider.fetchRates(anchor);
            int updated = currencyService.refreshRates(rates);
            log.info("Exchange rates refreshed: {} currencies updated via provider '{}'", updated, provider.name());
            return updated;
        } catch (Exception e) {
            log.warn("Exchange rate refresh failed: {}", e.getMessage());
            return -1;
        }
    }
}
