package com.example.ecommerce_backend.modules.role.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleAlreadyExistsException extends BaseException {
    public RoleAlreadyExistsException(String name) {
        super("Role '" + name + "' already exists", HttpStatus.CONFLICT);
    }
}
