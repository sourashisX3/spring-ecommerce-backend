package com.example.ecommerce_backend.modules.user.dto.response;

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
@Schema(description = "User response")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "User UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "First name", example = "John")
    private String firstName;
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
    @Schema(description = "Dial code", example = "+1")
    private String dialCode;
    @Schema(description = "Phone number", example = "1234567890")
    private String phoneNumber;
    @Schema(description = "Profile picture URL", example = "https://example.com/profile.jpg")
    private String profilePictureUrl;
    @Schema(description = "Role name", example = "ROLE_USER")
    private String roleName;
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
    @Schema(description = "Whether the user is active", example = "true")
    private boolean isActive;
    @Schema(description = "Whether email is verified", example = "true")
    private boolean isEmailVerified;
    @Schema(description = "Whether phone is verified", example = "false")
    private boolean isPhoneVerified;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("isEmailVerified")
    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    @JsonProperty("isEmailVerified")
    public void setEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    @JsonProperty("isPhoneVerified")
    public boolean isPhoneVerified() {
        return isPhoneVerified;
    }

    @JsonProperty("isPhoneVerified")
    public void setPhoneVerified(boolean isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }
}
