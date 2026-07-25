package com.example.ecommerce_backend.modules.coupon.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CouponExhaustedException extends BaseException {

    public CouponExhaustedException(String code) {
        super("Coupon usage limit reached: " + code, HttpStatus.BAD_REQUEST);
    }

    public CouponExhaustedException(String message, HttpStatus status) {
        super(message, status);
    }
}
