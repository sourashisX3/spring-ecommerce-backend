package com.example.ecommerce_backend.modules.brand.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.brand.dto.request.BrandRequest;
import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.entity.Brand;
import com.example.ecommerce_backend.modules.brand.exception.BrandNotFoundException;
import com.example.ecommerce_backend.modules.brand.mapper.BrandMapper;
import com.example.ecommerce_backend.modules.brand.repository.BrandRepository;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<BrandResponse> getAll(Boolean active) {
        return brandRepository.findAll().stream()
                .filter(b -> active == null || b.isActive() == active)
                .map(b -> {
                    BrandResponse resp = BrandMapper.toResponse(b);
                    resp.setProductCount(productRepository.countByBrandId(b.getId()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<BrandResponse> getAll(String search, Boolean active, Pageable pageable) {
        return brandRepository.search(search, active, pageable)
                .map(b -> {
                    BrandResponse resp = BrandMapper.toResponse(b);
                    resp.setProductCount(productRepository.countByBrandId(b.getId()));
                    return resp;
                });
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
    public BrandResponse update(String uuid, BrandRequest request) {
        Brand brand = brandRepository.findByUuid(uuid)
                .orElseThrow(() -> new BrandNotFoundException(uuid));

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
    public boolean toggleStatus(String uuid, boolean isActive) {
        Brand brand = brandRepository.findByUuid(uuid)
                .orElseThrow(() -> new BrandNotFoundException(uuid));
        if (brand.isActive() == isActive) {
            return false;
        }
        brand.setActive(isActive);
        brandRepository.save(brand);
        return true;
    }

    @Transactional
    @RequiresPermission("brand:write")
    public void delete(String uuid) {
        Brand brand = brandRepository.findByUuid(uuid)
                .orElseThrow(() -> new BrandNotFoundException(uuid));
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
