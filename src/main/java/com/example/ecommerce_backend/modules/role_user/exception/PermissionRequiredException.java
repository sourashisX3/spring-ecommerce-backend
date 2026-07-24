package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionRequiredException extends BaseException {

    public PermissionRequiredException() {
        super("At least one permission is required to create a role", HttpStatus.BAD_REQUEST);
    }

}
