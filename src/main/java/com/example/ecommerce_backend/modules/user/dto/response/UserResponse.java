package com.example.ecommerce_backend.modules.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String dialCode;
    private String phoneNumber;
    private String profilePictureUrl;
    private String roleName;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private Long zipCode;
    private boolean isActive;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private Instant createdAt;
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
