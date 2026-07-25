package com.example.ecommerce_backend.modules.image.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.image.dto.request.ImageRequest;
import com.example.ecommerce_backend.modules.image.dto.response.ImageResponse;
import com.example.ecommerce_backend.modules.image.entity.ProductImage;
import com.example.ecommerce_backend.modules.image.exception.ImageNotFoundException;
import com.example.ecommerce_backend.modules.image.mapper.ImageMapper;
import com.example.ecommerce_backend.modules.image.repository.ProductImageRepository;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ProductImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @RequiresPermission("product:write")
    public ImageResponse addImage(String productUuid, ImageRequest request) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));

        if (request.isPrimary()) {
            product.getImages().forEach(img -> img.setPrimary(false));
        }

        ProductImage image = ProductImage.builder()
                .imageUrl(request.getImageUrl())
                .isPrimary(request.isPrimary())
                .sortOrder(request.getSortOrder())
                .product(product)
                .build();

        image = imageRepository.save(image);
        return ImageMapper.toImageResponse(image);
    }

    @Transactional
    @RequiresPermission("product:write")
    public void deleteImage(String imageUuid) {
        ProductImage image = imageRepository.findByUuid(imageUuid)
                .orElseThrow(() -> new ImageNotFoundException(imageUuid));

        boolean wasPrimary = image.isPrimary();
        Long productId = image.getProduct().getId();

        imageRepository.delete(image);

        if (wasPrimary) {
            List<ProductImage> remaining = imageRepository.findByProductId(productId);
            if (!remaining.isEmpty()) {
                ProductImage newPrimary = remaining.stream()
                        .min(Comparator.comparingInt(ProductImage::getSortOrder))
                        .orElse(remaining.get(0));
                newPrimary.setPrimary(true);
                imageRepository.save(newPrimary);
            }
        }
    }
}
