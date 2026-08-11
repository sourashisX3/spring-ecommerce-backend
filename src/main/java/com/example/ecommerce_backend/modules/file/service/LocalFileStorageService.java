package com.example.ecommerce_backend.modules.file.service;

import com.example.ecommerce_backend.modules.file.entity.StoredFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path storageRoot;

    public LocalFileStorageService(@Value("${app.storage.local.directory:./uploads}") String directory) {
        this.storageRoot = Paths.get(directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create storage directory: " + this.storageRoot, e);
        }
    }

    @Override
    public StoredFile store(MultipartFile file, StoredFile metadata) {
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = original.contains(".")
                ? original.substring(original.lastIndexOf('.')).toLowerCase()
                : "";
        String storedName = UUID.randomUUID() + extension;
        Path target = this.storageRoot.resolve(storedName).normalize();
        if (!target.startsWith(this.storageRoot)) {
            throw new IllegalStateException("Invalid file name: " + original);
        }
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file: " + original, e);
        }
        metadata.setStoredName(storedName);
        return metadata;
    }

    @Override
    public Resource load(StoredFile file) {
        Path path = this.storageRoot.resolve(file.getStoredName()).normalize();
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        } catch (IOException ignored) {
            // fall through to the not-found response below
        }
        throw new com.example.ecommerce_backend.modules.file.exception.FileNotFoundException(file.getUuid());
    }

    @Override
    public void delete(StoredFile file) {
        try {
            Path path = this.storageRoot.resolve(file.getStoredName()).normalize();
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // idempotent delete — nothing to clean up
        }
    }
}
