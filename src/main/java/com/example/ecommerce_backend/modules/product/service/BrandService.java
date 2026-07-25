package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.product.dto.request.BrandRequest;
import com.example.ecommerce_backend.modules.product.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.product.entity.Brand;
import com.example.ecommerce_backend.modules.product.exception.BrandNotFoundException;
import com.example.ecommerce_backend.modules.product.mapper.BrandMapper;
import com.example.ecommerce_backend.modules.product.repository.BrandRepository;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<BrandResponse> getAll() {
        return brandRepository.findAll().stream()
                .map(b -> {
                    BrandResponse resp = BrandMapper.toResponse(b);
                    resp.setProductCount(productRepository.countByBrandId(b.getId()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BrandResponse getBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new BrandNotFoundException(slug));
        BrandResponse resp = BrandMapper.toResponse(brand);
        resp.setProductCount(productRepository.countByBrandId(brand.getId()));
        return resp;
    }

    @Transactional
    @RequiresPermission("brand:write")
    public BrandResponse create(BrandRequest request) {
        Brand brand = Brand.builder()
                .name(request.getName())
                .slug(generateUniqueSlug(request.getName()))
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .website(request.getWebsite())
                .build();
        brand = brandRepository.save(brand);
        return BrandMapper.toResponse(brand);
    }

    @Transactional
    @RequiresPermission("brand:write")
    public BrandResponse update(String slug, BrandRequest request) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new BrandNotFoundException(slug));

        brand.setName(request.getName());
        if (!brand.getSlug().equals(generateSlug(request.getName()))) {
            brand.setSlug(generateUniqueSlug(request.getName()));
        }
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setWebsite(request.getWebsite());

        brand = brandRepository.save(brand);
        return BrandMapper.toResponse(brand);
    }

    @Transactional
    @RequiresPermission("brand:write")
    public void delete(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new BrandNotFoundException(slug));
        brandRepository.delete(brand);
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = generateSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (brandRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
