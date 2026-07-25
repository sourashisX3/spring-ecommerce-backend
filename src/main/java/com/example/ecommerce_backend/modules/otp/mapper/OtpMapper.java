package com.example.ecommerce_backend.modules.otp.mapper;

import com.example.ecommerce_backend.modules.otp.dto.response.OtpResponse;

public class OtpMapper {

    private OtpMapper() {
    }

    public static OtpResponse toOtpResponse(String message) {
        return OtpResponse.builder()
                .message(message)
                .build();
    }
}
