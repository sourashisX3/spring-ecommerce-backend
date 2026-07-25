package com.example.ecommerce_backend.modules.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;

    @Size(min = 1, max = 10, message = "Dial code must be between 1 and 10 characters")
    private String dialCode;

    private String phoneNumber;
    private String profilePictureUrl;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private Long zipCode;
}
