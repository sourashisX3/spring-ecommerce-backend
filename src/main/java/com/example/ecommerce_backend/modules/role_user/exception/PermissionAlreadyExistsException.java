package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionAlreadyExistsException extends BaseException {

    public PermissionAlreadyExistsException(String name) {
        super("Permission '" + name + "' already exists", HttpStatus.CONFLICT);
    }
}
