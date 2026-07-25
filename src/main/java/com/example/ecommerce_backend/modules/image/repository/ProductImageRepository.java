package com.example.ecommerce_backend.modules.image.repository;

import com.example.ecommerce_backend.modules.image.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);

    Optional<ProductImage> findByUuid(String uuid);
}
