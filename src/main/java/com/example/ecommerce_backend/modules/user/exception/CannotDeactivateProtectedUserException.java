package com.example.ecommerce_backend.modules.user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CannotDeactivateProtectedUserException extends BaseException {

    public CannotDeactivateProtectedUserException() {
        super("Cannot deactivate SUPER_ADMIN user", HttpStatus.FORBIDDEN);
    }
}
