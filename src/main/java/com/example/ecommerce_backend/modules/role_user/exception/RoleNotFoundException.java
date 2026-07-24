package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends BaseException {

    public RoleNotFoundException(Long id) {
        super("Role not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public RoleNotFoundException(){
        super("Role not found", HttpStatus.NOT_FOUND);
    }
}
