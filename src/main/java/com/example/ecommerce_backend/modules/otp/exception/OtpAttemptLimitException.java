package com.example.ecommerce_backend.modules.otp.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OtpAttemptLimitException extends BaseException {

    public OtpAttemptLimitException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
