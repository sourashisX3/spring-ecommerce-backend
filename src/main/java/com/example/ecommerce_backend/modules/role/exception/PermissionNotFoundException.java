package com.example.ecommerce_backend.modules.role.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionNotFoundException extends BaseException {

    public PermissionNotFoundException() {
        super("Permission not found", HttpStatus.NOT_FOUND);
    }

    public PermissionNotFoundException(String name) {
        super("Permission not found with name: " + name, HttpStatus.NOT_FOUND);
    }

    public PermissionNotFoundException(Long id) {
        super("Permission not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
