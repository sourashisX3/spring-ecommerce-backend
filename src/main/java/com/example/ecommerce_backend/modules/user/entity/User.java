package com.example.ecommerce_backend.modules.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private UserAddress address;
    private String password;
    private Role role;
    private boolean isActive;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;

    @Data
    @Entity
    @Getter
    @Setter
    @Table (name = "user_addresses")
    private static class UserAddress {
        private String city;
        private String state;
        private String country;
        private Long zipCode;
        private String streetAddress;

        public UserAddress(String city, String state, String country, Long zipCode, String streetAddress) {
            this.city = city;
            this.state = state;
            this.country = country;
            this.zipCode = zipCode;
            this.streetAddress = streetAddress;
        }
    }
}
