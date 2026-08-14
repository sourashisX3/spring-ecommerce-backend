package com.example.ecommerce_backend.modules.currency.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request object for updating a currency (partial update; only provided fields change)")
public class CurrencyUpdateRequest {

    @Schema(description = "Currency name", example = "US Dollar")
    private String name;

    @Schema(description = "Currency symbol", example = "$")
    private String symbol;

    @Schema(description = "Sort order", example = "0")
    private Integer sortOrder;

    @Schema(description = "Whether the currency is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Location (country) this currency is used for", example = "India")
    private String location;

    @Schema(description = "Value of 1 unit of this currency in INR", example = "83.9")
    private BigDecimal exchangeRate;
}
