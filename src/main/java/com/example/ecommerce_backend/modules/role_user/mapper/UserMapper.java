package com.example.ecommerce_backend.modules.role_user.mapper;

import com.example.ecommerce_backend.modules.role_user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import com.example.ecommerce_backend.modules.role_user.entity.UserAddress;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        UserAddress address = user.getAddress();
        return UserResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dialCode(user.getDialCode())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .streetAddress(address != null ? address.getStreetAddress() : null)
                .city(address != null ? address.getCity() : null)
                .state(address != null ? address.getState() : null)
                .country(address != null ? address.getCountry() : null)
                .zipCode(address != null ? address.getZipCode() : null)
                .isActive(user.isActive())
                .isEmailVerified(user.isEmailVerified())
                .isPhoneVerified(user.isPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
