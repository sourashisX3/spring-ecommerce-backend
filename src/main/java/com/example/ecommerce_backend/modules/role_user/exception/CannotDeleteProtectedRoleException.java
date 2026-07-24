package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CannotDeleteProtectedRoleException extends BaseException {

    public CannotDeleteProtectedRoleException(String roleName) {
        super("Cannot delete protected role: " + roleName, HttpStatus.FORBIDDEN);
    }
}
