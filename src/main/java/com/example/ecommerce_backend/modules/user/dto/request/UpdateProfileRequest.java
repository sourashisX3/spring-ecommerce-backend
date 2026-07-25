package com.example.ecommerce_backend.modules.user.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String firstName;
    private String lastName;
    private String dialCode;
    private String phoneNumber;
    private String profilePictureUrl;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private Long zipCode;
}
