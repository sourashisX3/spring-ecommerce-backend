package com.example.ecommerce_backend.modules.file.dto;

import com.example.ecommerce_backend.modules.file.entity.StoredFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Uploaded file response")
public class FileUploadResponse {

    @Schema(description = "File UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;

    @Schema(description = "Original file name", example = "product-photo.png")
    private String originalName;

    @Schema(description = "MIME type", example = "image/png")
    private String contentType;

    @Schema(description = "Size in bytes", example = "24576")
    private long size;

    @Schema(description = "Public URL to fetch the file", example = "/api/v1/files/550e8400-e29b-41d4-a716-446655440000")
    private String url;

    public static FileUploadResponse from(StoredFile file) {
        return FileUploadResponse.builder()
                .uuid(file.getUuid())
                .originalName(file.getOriginalName())
                .contentType(file.getContentType())
                .size(file.getSize())
                .url("/api/v1/files/" + file.getUuid())
                .build();
    }
}
