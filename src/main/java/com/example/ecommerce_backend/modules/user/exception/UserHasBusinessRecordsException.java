package com.example.ecommerce_backend.modules.user.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserHasBusinessRecordsException extends BaseException {

    public UserHasBusinessRecordsException() {
        super("This user has orders, payments or other business records and cannot be deleted. Deactivate the account instead.", HttpStatus.CONFLICT);
    }
}