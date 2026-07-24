package com.example.ecommerce_backend.modules.user.dto.request;

import com.example.ecommerce_backend.modules.user.entity.UserAddress;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Nullable
    private String profilePictureUrl;

    @Nullable
    private UserAddress address;

    @NotBlank(message = "Password is required")
    private String password;

}