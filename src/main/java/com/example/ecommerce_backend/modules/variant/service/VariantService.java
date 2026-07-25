package com.example.ecommerce_backend.modules.variant.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.DuplicateSkuException;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.variant.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.variant.dto.response.VariantResponse;
import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import com.example.ecommerce_backend.modules.variant.exception.ProductVariantNotFoundException;
import com.example.ecommerce_backend.modules.variant.mapper.VariantMapper;
import com.example.ecommerce_backend.modules.variant.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VariantService {

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<VariantResponse> getVariants(String productUuid) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        return product.getVariants().stream()
                .map(VariantMapper::toVariantResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VariantResponse getVariant(String variantUuid) {
        ProductVariant variant = variantRepository.findByUuid(variantUuid)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantUuid));
        return VariantMapper.toVariantResponse(variant);
    }

    @Transactional
    @RequiresPermission("product:write")
    public VariantResponse addVariant(String productUuid, VariantRequest request) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));

        if (variantRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        if (request.isDefault()) {
            product.getVariants().forEach(v -> v.setDefault(false));
        }

        ProductVariant variant = ProductVariant.builder()
                .sku(request.getSku())
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .attributes(request.getAttributes() != null ? request.getAttributes() : new HashMap<>())
                .isDefault(request.isDefault())
                .sortOrder(request.getSortOrder())
                .product(product)
                .build();

        variant = variantRepository.save(variant);
        return VariantMapper.toVariantResponse(variant);
    }

    @Transactional
    @RequiresPermission("product:write")
    public VariantResponse updateVariant(String variantUuid, VariantRequest request) {
        ProductVariant variant = variantRepository.findByUuid(variantUuid)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantUuid));

        if (!variant.getSku().equals(request.getSku()) && variantRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        variant.setSku(request.getSku());
        variant.setName(request.getName());
        variant.setPrice(request.getPrice());
        variant.setStock(request.getStock());
        variant.setAttributes(request.getAttributes() != null ? request.getAttributes() : new HashMap<>());
        variant.setDefault(request.isDefault());
        variant.setSortOrder(request.getSortOrder());

        if (request.isDefault()) {
            Product product = variant.getProduct();
            String currentUuid = variant.getUuid();
            product.getVariants().stream()
                    .filter(v -> !v.getUuid().equals(currentUuid))
                    .forEach(v -> v.setDefault(false));
        }

        variant = variantRepository.save(variant);
        return VariantMapper.toVariantResponse(variant);
    }

    @Transactional
    @RequiresPermission("product:write")
    public void deleteVariant(String variantUuid) {
        ProductVariant variant = variantRepository.findByUuid(variantUuid)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantUuid));
        variantRepository.delete(variant);
    }
}
