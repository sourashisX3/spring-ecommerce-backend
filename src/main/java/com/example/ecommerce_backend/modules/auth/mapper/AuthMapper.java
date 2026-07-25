package com.example.ecommerce_backend.modules.auth.mapper;

import com.example.ecommerce_backend.modules.auth.dto.request.RegisterRequest;
import com.example.ecommerce_backend.modules.auth.dto.response.AuthResponse;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.entity.UserAddress;

public class AuthMapper {

    private AuthMapper() {
    }

    public static User toUser(RegisterRequest request, String encodedPassword, Role role) {
        UserAddress address = new UserAddress();
        address.setStreetAddress(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dialCode(request.getDialCode())
                .phoneNumber(request.getPhoneNumber())
                .password(encodedPassword)
                .address(address)
                .role(role)
                .isActive(true)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .build();
    }

    public static AuthResponse toAuthResponse(String accessToken, String refreshToken, UserResponse user) {
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(user)
                .build();
    }
}
