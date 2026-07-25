package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AuthenticationRequiredException extends BaseException {

    public AuthenticationRequiredException() {
        super("Authentication required", HttpStatus.UNAUTHORIZED);
    }
}
