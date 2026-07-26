package com.example.ecommerce_backend.modules.shipping.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address response")
public class AddressResponse {

    @Schema(description = "Address ID", example = "1")
    private Long id;
    @Schema(description = "Address UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Address label", example = "Home")
    private String label;
    @Schema(description = "Recipient name", example = "John Doe")
    private String recipientName;
    @Schema(description = "Phone number", example = "+1234567890")
    private String phone;
    @Schema(description = "Address line 1", example = "123 Main St")
    private String addressLine1;
    @Schema(description = "Address line 2", example = "Apt 4B")
    private String addressLine2;
    @Schema(description = "City", example = "New York")
    private String city;
    @Schema(description = "State", example = "NY")
    private String state;
    @Schema(description = "Postal code", example = "10001")
    private String postalCode;
    @Schema(description = "Country", example = "USA")
    private String country;

    @JsonProperty("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    @JsonProperty("isDefault")
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Schema(description = "Whether this is the default address", example = "false")
    private boolean isDefault;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
