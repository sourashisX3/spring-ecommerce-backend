package com.example.ecommerce_backend.modules.product.repository;

import com.example.ecommerce_backend.modules.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);
}
