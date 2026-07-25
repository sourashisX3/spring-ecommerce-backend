package com.example.ecommerce_backend.modules.auth.dto.response;

import com.example.ecommerce_backend.modules.user.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserResponse user;
}
