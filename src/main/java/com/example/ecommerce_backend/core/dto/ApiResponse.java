package com.example.ecommerce_backend.core.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "HTTP status code", example = "200")
    private int statusCode;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response payload")
    private T response;

    @Schema(description = "Pagination metadata — present only on paginated endpoints")
    private Pagination pagination;

    // ── Non-paginated ──

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(message)
                        .response(data)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<T>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(message)
                        .response(data)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ApiResponse.<T>builder()
                        .statusCode(status.value())
                        .message(message)
                        .build());
    }

    // ── Paginated ──

    public static <T> ResponseEntity<ApiResponse<T>> paginated(T data, String message, Pagination pagination) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(message)
                        .response(data)
                        .pagination(pagination)
                        .build());
    }
}

