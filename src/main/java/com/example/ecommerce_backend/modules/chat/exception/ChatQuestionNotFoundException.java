package com.example.ecommerce_backend.modules.chat.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ChatQuestionNotFoundException extends BaseException {
    public ChatQuestionNotFoundException(String uuid) {
        super("Chat question not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
