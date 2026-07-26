package com.example.ecommerce_backend.modules.chat.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ChatRoomNotFoundException extends BaseException {
    public ChatRoomNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
