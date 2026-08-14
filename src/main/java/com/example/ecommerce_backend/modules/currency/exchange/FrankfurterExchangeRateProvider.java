package com.example.ecommerce_backend.modules.currency.exchange;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Exchange rate provider backed by Frankfurter (https://frankfurter.dev).
 * Free, no API key, daily rates sourced from central banks.
 */
@Component
public class FrankfurterExchangeRateProvider implements ExchangeRateProvider {

    private static final Logger log = LoggerFactory.getLogger(FrankfurterExchangeRateProvider.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;
    private final Duration timeout;

    @Autowired
    public FrankfurterExchangeRateProvider(
            @Value("${app.currency.provider.frankfurter.url:https://api.frankfurter.dev/v1/latest}") String baseUrl,
            @Value("${app.currency.provider.frankfurter.timeout-ms:10000}") long timeoutMs) {
        this.baseUrl = baseUrl;
        this.timeout = Duration.ofMillis(timeoutMs);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public String name() {
        return "frankfurter";
    }

    @Override
    public Map<String, BigDecimal> fetchRates(String anchor) throws Exception {
        URI uri = URI.create(baseUrl + "?base=" + anchor.toUpperCase());
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Frankfurter returned HTTP " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode rates = root.path("rates");
        if (!rates.isObject()) {
            throw new IllegalStateException("Frankfurter payload missing 'rates' object");
        }

        Map<String, BigDecimal> result = new HashMap<>();
        rates.fields().forEachRemaining(entry -> {
            String code = entry.getKey().toUpperCase();
            if (code.matches("[A-Z]{3}") && entry.getValue().isNumber()) {
                BigDecimal raw = entry.getValue().decimalValue();
                // Frankfurter quotes with base=INR: value = how many units of this
                // currency one INR buys (e.g. USD ~0.0119). We store the inverse:
                // the price of one unit of the currency in INR (USD ~84.0).
                if (raw.compareTo(BigDecimal.ZERO) > 0) {
                    result.put(code, BigDecimal.ONE.divide(raw, 6, RoundingMode.HALF_UP));
                }
            }
        });

        // The anchor currency is always worth exactly 1.0 in its own units.
        result.put(anchor.toUpperCase(), BigDecimal.ONE);

        if (result.isEmpty()) {
            throw new IllegalStateException("Frankfurter returned no usable rates");
        }
        log.info("Fetched {} exchange rates from Frankfurter (base={})", result.size(), anchor.toUpperCase());
        return result;
    }
}
