package com.example.ecommerce_backend.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Registration request")
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Dial code is required")
    @Schema(description = "Country dial code", example = "+1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dialCode;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Password (min 6 characters)", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Street address", example = "123 Main St")
    private String streetAddress;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "State", example = "NY")
    private String state;

    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description = "ZIP code", example = "10001")
    private Long zipCode;
}
