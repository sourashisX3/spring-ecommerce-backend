package com.example.ecommerce_backend.modules.file.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FileNotFoundException extends BaseException {

    public FileNotFoundException(String uuid) {
        super("File not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
