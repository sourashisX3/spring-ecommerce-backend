package com.example.ecommerce_backend.core.exception;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> details.add(error.getField() + " - " + error.getDefaultMessage()));

        ApiResponse<List<?>> body = ApiResponse.<List<?>>builder()
                .statusCode(status.value())
                .message("Validation failed: " + String.join(", ", details))
                .response(Collections.emptyList())
                .build();

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
        log.warn("BaseException: {} - {}", ex.getStatus(), ex.getMessage());
        return ApiResponse.error(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<?>>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException: {}", ex.getMessage());
        List<String> details = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation ->
                details.add(violation.getPropertyPath().toString() + " - " + violation.getMessage())
        );
        ApiResponse<List<?>> body = ApiResponse.<List<?>>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed: " + String.join(", ", details))
                .response(Collections.emptyList())
                .build();
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandledException(Exception ex, WebRequest request) {
        log.error("Unhandled exception at {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }
}
