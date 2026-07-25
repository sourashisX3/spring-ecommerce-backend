package com.example.ecommerce_backend.modules.shipping.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DeliveryNotFoundException extends BaseException {

    public DeliveryNotFoundException(String uuid) {
        super("Delivery not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
