package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PhoneAlreadyExistsException extends BaseException {

    public PhoneAlreadyExistsException(String phone) {
        super("Phone number already registered: " + phone, HttpStatus.CONFLICT);
    }
}
