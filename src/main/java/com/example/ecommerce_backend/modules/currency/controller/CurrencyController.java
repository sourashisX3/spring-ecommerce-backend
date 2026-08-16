package com.example.ecommerce_backend.modules.currency.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.currency.dto.request.CurrencyRequest;
import com.example.ecommerce_backend.modules.currency.dto.request.CurrencyUpdateRequest;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exchange.ExchangeRateService;
import com.example.ecommerce_backend.modules.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@Tag(name = "Currency", description = "Currency management APIs")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping
    @RequiresPermission("currency:read")
    @Operation(summary = "Get all currencies", description = "Retrieves all currencies, optionally filtered")
    public ResponseEntity<ApiResponse<List<Currency>>> getAllCurrencies(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String location
    ) {
        return ApiResponse.success(currencyService.getAllCurrencies(active, location),
                "Currencies retrieved successfully");
    }

    @GetMapping("/default")
    @RequiresPermission("currency:read")
    @Operation(summary = "Get default currency", description = "Retrieves the active default currency")
    public ResponseEntity<ApiResponse<Currency>> getDefault() {
        return ApiResponse.success(currencyService.getDefault(), "Default currency retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @RequiresPermission("currency:read")
    @Operation(summary = "Get currency by UUID", description = "Retrieves a single currency by its UUID")
    public ResponseEntity<ApiResponse<Currency>> getByUuid(@PathVariable String uuid) {
        return ApiResponse.success(currencyService.getByUuid(uuid), "Currency retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("currency:write")
    @Operation(summary = "Create a currency", description = "Creates a new currency")
    public ResponseEntity<ApiResponse<Currency>> create(@Valid @RequestBody CurrencyRequest request) {
        Currency currency = currencyService.createCurrency(request);
        return ApiResponse.created(currency, "Currency created successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("currency:write")
    @Operation(summary = "Toggle currency status", description = "Activates or deactivates a currency")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = currencyService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Currency status updated successfully" : "Currency is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @PatchMapping("/{uuid}/default")
    @RequiresPermission("currency:write")
    @Operation(summary = "Set default currency", description = "Makes the currency the store default")
    public ResponseEntity<ApiResponse<Currency>> makeDefault(@PathVariable String uuid) {
        return ApiResponse.success(currencyService.makeDefault(uuid),
                "Default currency updated successfully");
    }

    @PatchMapping("/{uuid}")
    @RequiresPermission("currency:write")
    @Operation(summary = "Update currency", description = "Updates currency details (name, symbol, location, sort order, status, exchange rate)")
    public ResponseEntity<ApiResponse<Currency>> update(
            @PathVariable String uuid,
            @RequestBody CurrencyUpdateRequest request) {
        return ApiResponse.success(currencyService.updateCurrency(uuid, request),
                "Currency updated successfully");
    }

    @PostMapping("/refresh-rates")
    @RequiresPermission("currency:write")
    @Operation(summary = "Refresh exchange rates", description = "Fetches the latest exchange rates from the configured provider and stores them")
    public ResponseEntity<ApiResponse<Integer>> refreshRates() {
        int updated = exchangeRateService.refreshRates();
        if (updated < 0) {
            return ApiResponse.success(updated, "Exchange rate refresh failed; using last known rates");
        }
        return ApiResponse.success(updated, "Exchange rates refreshed successfully");
    }
}
