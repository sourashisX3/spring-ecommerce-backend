package com.example.ecommerce_backend.modules.role_user.dto.response;

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
}
