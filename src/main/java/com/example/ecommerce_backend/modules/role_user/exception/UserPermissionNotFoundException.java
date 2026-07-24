package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserPermissionNotFoundException extends BaseException {

    public UserPermissionNotFoundException(Long id) {
        super("User permission not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
