package com.example.ecommerce_backend.modules.discount.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.discount.dto.request.DiscountTypeRequest;
import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.exception.DiscountTypeNotFoundException;
import com.example.ecommerce_backend.modules.discount.mapper.DiscountMapper;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountTypeService {

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

    @Transactional
    @RequiresPermission("discount:write")
    public DiscountTypeResponse create(DiscountTypeRequest request) {
        DiscountType type = DiscountType.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .computation(request.getComputation())
                .configSchema(request.getConfigSchema())
                .build();

        type = discountTypeRepository.save(type);
        return DiscountMapper.toTypeResponse(type);
    }

    @Transactional(readOnly = true)
    public DiscountTypeResponse getByUuid(String uuid) {
        DiscountType type = discountTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountTypeNotFoundException(uuid));
        return DiscountMapper.toTypeResponse(type);
    }

    @Transactional(readOnly = true)
    public DiscountTypeResponse getByCode(String code) {
        DiscountType type = discountTypeRepository.findByCode(code)
                .orElseThrow(() -> new DiscountTypeNotFoundException(code));
        return DiscountMapper.toTypeResponse(type);
    }

    @Transactional(readOnly = true)
    public List<DiscountTypeResponse> getAll() {
        return discountTypeRepository.findAll().stream()
                .map(DiscountMapper::toTypeResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequiresPermission("discount:write")
    public DiscountTypeResponse update(String uuid, DiscountTypeRequest request) {
        DiscountType type = discountTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountTypeNotFoundException(uuid));

        type.setCode(request.getCode());
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setComputation(request.getComputation());
        type.setConfigSchema(request.getConfigSchema());

        type = discountTypeRepository.save(type);
        return DiscountMapper.toTypeResponse(type);
    }

    @Transactional
    @RequiresPermission("discount:write")
    public void toggleStatus(String uuid, boolean active) {
        DiscountType type = discountTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountTypeNotFoundException(uuid));
        type.setActive(active);
        discountTypeRepository.save(type);
    }

    @Transactional
    @RequiresPermission("discount:write")
    public void delete(String uuid) {
        DiscountType type = discountTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new DiscountTypeNotFoundException(uuid));
        discountTypeRepository.delete(type);
    }
}
