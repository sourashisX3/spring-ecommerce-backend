package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnTypeRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnTypeResponse;
import com.example.ecommerce_backend.modules.returns.entity.ReturnType;
import com.example.ecommerce_backend.modules.returns.mapper.ReturnMapper;
import com.example.ecommerce_backend.modules.returns.repository.ReturnTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnTypeService {

    @Autowired
    private ReturnTypeRepository returnTypeRepository;

    @Transactional(readOnly = true)
    public List<ReturnTypeResponse> getAll() {
        return returnTypeRepository.findAll().stream()
                .map(ReturnMapper::toReturnTypeResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReturnTypeResponse getByUuid(String uuid) {
        ReturnType entity = returnTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Return type not found: " + uuid, HttpStatus.NOT_FOUND));
        return ReturnMapper.toReturnTypeResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public ReturnTypeResponse create(ReturnTypeRequest request) {
        if (returnTypeRepository.findByCode(request.getCode()).isPresent()) {
            throw new BaseException("Return type code already exists: " + request.getCode(), HttpStatus.CONFLICT);
        }
        ReturnType entity = ReturnType.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
        entity = returnTypeRepository.save(entity);
        return ReturnMapper.toReturnTypeResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public ReturnTypeResponse update(String uuid, ReturnTypeRequest request) {
        ReturnType entity = returnTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Return type not found: " + uuid, HttpStatus.NOT_FOUND));
        if (!entity.getCode().equals(request.getCode())
                && returnTypeRepository.findByCode(request.getCode()).isPresent()) {
            throw new BaseException("Return type code already exists: " + request.getCode(), HttpStatus.CONFLICT);
        }
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity = returnTypeRepository.save(entity);
        return ReturnMapper.toReturnTypeResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public void delete(String uuid) {
        ReturnType entity = returnTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Return type not found: " + uuid, HttpStatus.NOT_FOUND));
        returnTypeRepository.delete(entity);
    }
}
