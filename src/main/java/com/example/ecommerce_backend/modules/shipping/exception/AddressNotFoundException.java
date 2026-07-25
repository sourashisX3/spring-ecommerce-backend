package com.example.ecommerce_backend.modules.shipping.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends BaseException {

    public AddressNotFoundException(String uuid) {
        super("Address not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
