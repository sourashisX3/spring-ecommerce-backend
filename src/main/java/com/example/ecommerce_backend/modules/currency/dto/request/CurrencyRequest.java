package com.example.ecommerce_backend.modules.currency.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a currency")
public class CurrencyRequest {

    @Schema(description = "ISO 4217 currency code", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Currency code is required")
    private String code;

    @Schema(description = "Currency name", example = "US Dollar", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Currency name is required")
    private String name;

    @Schema(description = "Currency symbol", example = "$")
    private String symbol;

    @Schema(description = "Sort order", example = "0")
    private int sortOrder;

    @Schema(description = "Whether the currency is active", example = "true")
    @NotNull(message = "isActive is required")
    private Boolean isActive;

    @Schema(description = "Location (country) this currency is used for", example = "India")
    private String location;

    @Schema(description = "Whether this is the default currency", example = "false")
    private Boolean isDefault;
}
