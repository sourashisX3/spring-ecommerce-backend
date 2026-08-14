package com.example.ecommerce_backend.modules.currency.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically refreshes stored exchange rates. A transient external outage
 * never crashes the application; failures are logged by {@link ExchangeRateService}.
 */
@Component
public class ExchangeRateScheduler {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateScheduler(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Scheduled(cron = "${app.currency.rates-cron:0 0 6 * * *}")
    public void refreshRates() {
        exchangeRateService.refreshRates();
    }
}
