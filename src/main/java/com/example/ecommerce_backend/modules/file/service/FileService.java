package com.example.ecommerce_backend.modules.file.service;

import com.example.ecommerce_backend.modules.file.dto.FileUploadResponse;
import com.example.ecommerce_backend.modules.file.entity.StoredFile;
import com.example.ecommerce_backend.modules.file.exception.FileNotFoundException;
import com.example.ecommerce_backend.modules.file.repository.StoredFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml",
            "application/pdf", "text/plain"
    );

    private final StoredFileRepository fileRepository;
    private final FileStorageService storageService;

    public FileService(StoredFileRepository fileRepository, FileStorageService storageService) {
        this.fileRepository = fileRepository;
        this.storageService = storageService;
    }

    @Transactional
    public FileUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File exceeds the maximum size of 5 MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + (contentType == null ? "unknown" : contentType));
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        StoredFile metadata = StoredFile.builder()
                .originalName(originalName)
                .contentType(contentType)
                .size(file.getSize())
                .build();

        storageService.store(file, metadata);
        metadata = fileRepository.save(metadata);
        return FileUploadResponse.from(metadata);
    }

    @Transactional(readOnly = true)
    public StoredFile getByUuid(String uuid) {
        return fileRepository.findByUuid(uuid)
                .orElseThrow(() -> new FileNotFoundException(uuid));
    }

    public Resource loadResource(StoredFile file) {
        return storageService.load(file);
    }

    @Transactional
    public void delete(String uuid) {
        StoredFile file = fileRepository.findByUuid(uuid)
                .orElseThrow(() -> new FileNotFoundException(uuid));
        fileRepository.delete(file);
        storageService.delete(file);
    }
}
