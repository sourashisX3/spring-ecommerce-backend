package com.example.ecommerce_backend.modules.role_user.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserAddress {
    private String city;
    private String state;
    private String country;
    private Long zipCode;
    private String streetAddress;
}
