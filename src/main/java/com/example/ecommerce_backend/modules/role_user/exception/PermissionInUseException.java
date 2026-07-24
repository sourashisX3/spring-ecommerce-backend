package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionInUseException extends BaseException {

    public PermissionInUseException(String permissionName) {
        super("Permission '" + permissionName + "' is assigned to roles or users and cannot be deleted", HttpStatus.CONFLICT);
    }
}
