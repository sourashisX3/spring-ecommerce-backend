package com.example.ecommerce_backend.modules.file.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.file.dto.FileUploadResponse;
import com.example.ecommerce_backend.modules.file.entity.StoredFile;
import com.example.ecommerce_backend.modules.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@Tag(name = "File", description = "File upload and management API")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file", description = "Uploads a single file and stores it locally (S3-ready storage abstraction)")
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(@RequestPart("file") MultipartFile file) {
        FileUploadResponse uploaded = fileService.upload(file);
        return ApiResponse.created(uploaded, "File uploaded successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Serve a file", description = "Returns the stored file content for a given UUID")
    public ResponseEntity<Resource> serve(@PathVariable String uuid) {
        StoredFile file = fileService.getByUuid(uuid);
        Resource resource = fileService.loadResource(file);
        String contentType = file.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : file.getContentType();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .body(resource);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a file", description = "Deletes a stored file and its metadata by UUID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        fileService.delete(uuid);
        return ApiResponse.success(null, "File deleted successfully");
    }
}
