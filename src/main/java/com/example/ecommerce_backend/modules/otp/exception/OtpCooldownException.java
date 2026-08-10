package com.example.ecommerce_backend.modules.otp.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OtpCooldownException extends BaseException {

    public OtpCooldownException(long waitSeconds) {
        super("Please wait " + waitSeconds + " seconds before requesting a new OTP", HttpStatus.TOO_MANY_REQUESTS);
    }
}
