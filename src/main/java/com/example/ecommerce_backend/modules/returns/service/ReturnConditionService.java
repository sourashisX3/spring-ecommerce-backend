package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnConditionRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnConditionResponse;
import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import com.example.ecommerce_backend.modules.returns.mapper.ReturnMapper;
import com.example.ecommerce_backend.modules.returns.repository.ReturnConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnConditionService {

    @Autowired
    private ReturnConditionRepository returnConditionRepository;

    @Transactional(readOnly = true)
    public List<ReturnConditionResponse> getAll() {
        return returnConditionRepository.findAll().stream()
                .map(ReturnMapper::toReturnConditionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReturnConditionResponse getByUuid(String uuid) {
        ReturnCondition entity = returnConditionRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Return condition not found: " + uuid, HttpStatus.NOT_FOUND));
        return ReturnMapper.toReturnConditionResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public ReturnConditionResponse create(ReturnConditionRequest request) {
        if (returnConditionRepository.findByCode(request.getCode()).isPresent()) {
            throw new BaseException("Return condition code already exists: " + request.getCode(), HttpStatus.CONFLICT);
        }
        ReturnCondition entity = ReturnCondition.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
        entity = returnConditionRepository.save(entity);
        return ReturnMapper.toReturnConditionResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public ReturnConditionResponse update(String uuid, ReturnConditionRequest request) {
        ReturnCondition entity = returnConditionRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Return condition not found: " + uuid, HttpStatus.NOT_FOUND));
        if (!entity.getCode().equals(request.getCode())
                && returnConditionRepository.findByCode(request.getCode()).isPresent()) {
            throw new BaseException("Return condition code already exists: " + request.getCode(), HttpStatus.CONFLICT);
        }
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity = returnConditionRepository.save(entity);
        return ReturnMapper.toReturnConditionResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public void delete(String uuid) {
        ReturnCondition entity = returnConditionRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Return condition not found: " + uuid, HttpStatus.NOT_FOUND));
        returnConditionRepository.delete(entity);
    }
}
