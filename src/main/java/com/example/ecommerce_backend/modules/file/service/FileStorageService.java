package com.example.ecommerce_backend.modules.file.service;

import com.example.ecommerce_backend.modules.file.entity.StoredFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Storage abstraction for uploaded files.
 *
 * The current implementation ({@link LocalFileStorageService}) writes files to a
 * local directory. To move to S3 / DigitalOcean Spaces later, add a new
 * implementation of this interface and switch the active bean — no controller,
 * service or entity changes are required.
 */
public interface FileStorageService {

    /**
     * Persist the uploaded file bytes and return metadata with the storage key
     * used to locate the file later.
     */
    StoredFile store(MultipartFile file, StoredFile metadata);

    /**
     * Load the file content as a resolvable resource.
     */
    Resource load(StoredFile file);

    /**
     * Delete the stored bytes for the given file. Idempotent — missing files
     * are ignored so callers can safely delete after DB removal.
     */
    void delete(StoredFile file);
}
