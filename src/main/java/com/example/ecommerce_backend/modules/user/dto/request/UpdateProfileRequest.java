package com.example.ecommerce_backend.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Update profile request")
public class UpdateProfileRequest {

    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Size(min = 1, max = 10, message = "Dial code must be between 1 and 10 characters")
    @Schema(description = "Dial code", example = "+1")
    private String dialCode;

    @Schema(description = "Phone number", example = "1234567890")
    private String phoneNumber;
    @Schema(description = "Profile picture URL", example = "https://example.com/profile.jpg")
    private String profilePictureUrl;
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
