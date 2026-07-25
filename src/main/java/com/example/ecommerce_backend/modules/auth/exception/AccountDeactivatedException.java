package com.example.ecommerce_backend.modules.auth.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountDeactivatedException extends BaseException {

    public AccountDeactivatedException() {
        super("Account has been deactivated", HttpStatus.FORBIDDEN);
    }
}
