package com.example.ecommerce_backend.modules.currency.exchange;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Strategy for fetching exchange rates from an external data source.
 *
 * <p>The returned map keys are ISO 4217 currency codes (uppercase, e.g. "USD")
 * and each value is the price of one unit of that currency expressed in the
 * anchor currency (INR). The anchor currency itself must map to {@code 1.0}.
 *
 * <p>New data sources can be plugged in by implementing this interface and
 * registering a {@code @Component}; the active provider is selected with the
 * {@code app.currency.rate-provider} configuration property.
 */
public interface ExchangeRateProvider {

    /** Unique identifier used by the {@code app.currency.rate-provider} property. */
    String name();

    /**
     * Fetches the latest exchange rates relative to the given anchor currency.
     *
     * @param anchor ISO 4217 code of the anchor currency (always INR for this app)
     * @return map of currency code to rate (value of 1 unit in anchor units)
     * @throws Exception when the source is unreachable or the payload is invalid
     */
    Map<String, BigDecimal> fetchRates(String anchor) throws Exception;
}
