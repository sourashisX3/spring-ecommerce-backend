package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.returns.exception.ReturnConditionConflictException;
import com.example.ecommerce_backend.modules.returns.exception.ReturnConditionNotFoundException;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnConditionRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnConditionResponse;
import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import com.example.ecommerce_backend.modules.returns.mapper.ReturnMapper;
import com.example.ecommerce_backend.modules.returns.repository.ReturnConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
                .orElseThrow(() -> new ReturnConditionNotFoundException("Return condition not found: " + uuid));
        return ReturnMapper.toReturnConditionResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public ReturnConditionResponse create(ReturnConditionRequest request) {
        if (returnConditionRepository.findByCode(request.getCode()).isPresent()) {
            throw new ReturnConditionConflictException("Return condition code already exists: " + request.getCode());
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
                .orElseThrow(() -> new ReturnConditionNotFoundException("Return condition not found: " + uuid));
        if (!entity.getCode().equals(request.getCode())
                && returnConditionRepository.findByCode(request.getCode()).isPresent()) {
            throw new ReturnConditionConflictException("Return condition code already exists: " + request.getCode());
        }
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity = returnConditionRepository.save(entity);
        return ReturnMapper.toReturnConditionResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public boolean toggleStatus(String uuid, boolean isActive) {
        ReturnCondition entity = returnConditionRepository.findByUuid(uuid)
                .orElseThrow(() -> new ReturnConditionNotFoundException("Return condition not found: " + uuid));
        if (entity.isActive() == isActive) {
            return false;
        }
        entity.setActive(isActive);
        returnConditionRepository.save(entity);
        return true;
    }

    @Transactional
    @RequiresPermission("return:write")
    public void delete(String uuid) {
        ReturnCondition entity = returnConditionRepository.findByUuid(uuid)
                .orElseThrow(() -> new ReturnConditionNotFoundException("Return condition not found: " + uuid));
        returnConditionRepository.delete(entity);
    }
}
