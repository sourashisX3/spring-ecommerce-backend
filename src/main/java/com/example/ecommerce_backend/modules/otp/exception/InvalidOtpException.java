package com.example.ecommerce_backend.modules.otp.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidOtpException extends BaseException {

    public InvalidOtpException() {
        super("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
    }
}
