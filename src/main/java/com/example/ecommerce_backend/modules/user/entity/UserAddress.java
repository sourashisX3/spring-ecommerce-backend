package com.example.ecommerce_backend.modules.user.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserAddress {
    private String streetAddress;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private Long zipCode;
}
