package com.example.ecommerce_backend.modules.role_user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicatePermissionAssignmentException extends BaseException {

    public DuplicatePermissionAssignmentException(Long userId, Long permissionId) {
        super("Permission " + permissionId + " is already assigned to user " + userId, HttpStatus.CONFLICT);
    }
}
