package com.example.ecommerce_backend.modules.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Address request")
public class AddressRequest {

    @Schema(description = "Address label", example = "Home")
    private String label;

    @NotBlank
    @Schema(description = "Recipient full name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientName;

    @NotBlank
    @Schema(description = "Phone number", example = "+1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank
    @Schema(description = "Address line 1", example = "123 Main St", requiredMode = Schema.RequiredMode.REQUIRED)
    private String addressLine1;

    @Schema(description = "Address line 2", example = "Apt 4B")
    private String addressLine2;

    @NotBlank
    @Schema(description = "City", example = "New York", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank
    @Schema(description = "State", example = "NY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String state;

    @NotBlank
    @Schema(description = "Postal code", example = "10001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String postalCode;

    @NotBlank
    @Schema(description = "Country", example = "USA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;

    @Schema(description = "Whether this is the default address", example = "false")
    private boolean isDefault;
}
