package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.exception.OrderNotFoundException;
import com.example.ecommerce_backend.modules.order.repository.OrderRepository;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnRequestDto;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnResponse;
import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import com.example.ecommerce_backend.modules.returns.entity.ReturnItem;
import com.example.ecommerce_backend.modules.returns.entity.ReturnRequest;
import com.example.ecommerce_backend.modules.returns.entity.ReturnType;
import com.example.ecommerce_backend.modules.returns.exception.InvalidReturnStateException;
import com.example.ecommerce_backend.modules.returns.exception.ReturnNotFoundException;
import com.example.ecommerce_backend.modules.returns.mapper.ReturnMapper;
import com.example.ecommerce_backend.modules.returns.entity.ReturnStatus;
import com.example.ecommerce_backend.modules.returns.repository.ReturnConditionRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnItemRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnRequestRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnStatusRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnTypeRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnService {

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Autowired
    private ReturnItemRepository returnItemRepository;

    @Autowired
    private ReturnTypeRepository returnTypeRepository;

    @Autowired
    private ReturnConditionRepository returnConditionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReturnStatusRepository returnStatusRepository;

    @Transactional(readOnly = true)
    public List<ReturnResponse> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    public Page<ReturnResponse> getAll(Pageable pageable) {
        return returnRequestRepository.findAll(pageable)
                .map(ReturnMapper::toReturnResponse);
    }

    @Transactional(readOnly = true)
    public ReturnResponse getByUuid(String uuid) {
        ReturnRequest entity = returnRequestRepository.findByUuid(uuid)
                .orElseThrow(() -> new ReturnNotFoundException(uuid));
        return ReturnMapper.toReturnResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ReturnResponse> getByUserId(Long userId) {
        return getByUserId(userId, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    public Page<ReturnResponse> getByUserId(Long userId, Pageable pageable) {
        return returnRequestRepository.findByUserId(userId, pageable)
                .map(ReturnMapper::toReturnResponse);
    }

    @Transactional
    public ReturnResponse create(User user, ReturnRequestDto request) {
        Order order = orderRepository.findByUuid(request.getOrderUuid())
                .orElseThrow(() -> new OrderNotFoundException(request.getOrderUuid()));

        ReturnType returnType = null;
        if (request.getReturnTypeCode() != null) {
            returnType = returnTypeRepository.findByCode(request.getReturnTypeCode())
                    .orElse(null);
        }

        ReturnStatus pendingStatus = returnStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new RuntimeException("ReturnStatus not found: PENDING"));

        ReturnRequest entity = ReturnRequest.builder()
                .user(user)
                .order(order)
                .returnType(returnType)
                .status(pendingStatus)
                .reason(request.getReason())
                .build();
        final ReturnRequest finalEntity = entity;

        List<ReturnItem> items = request.getItems().stream()
                .map(itemDto -> {
                    ReturnCondition condition = null;
                    if (itemDto.getConditionCode() != null) {
                        condition = returnConditionRepository.findByCode(itemDto.getConditionCode())
                                .orElse(null);
                    }
                    return ReturnItem.builder()
                            .returnRequest(finalEntity)
                            .orderItemId(itemDto.getOrderItemId())
                            .quantity(itemDto.getQuantity())
                            .condition(condition)
                            .conditionNote(itemDto.getConditionNote())
                            .build();
                })
                .collect(Collectors.toList());

        entity.setItems(items);
        entity = returnRequestRepository.save(entity);
        return ReturnMapper.toReturnResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public ReturnResponse updateStatus(String uuid, String statusCode, String resolutionNotes) {
        ReturnRequest entity = returnRequestRepository.findByUuid(uuid)
                .orElseThrow(() -> new ReturnNotFoundException(uuid));

        validateStatusTransition(entity.getStatus().getCode(), statusCode);

        ReturnStatus newStatus = returnStatusRepository.findByCode(statusCode)
                .orElseThrow(() -> new RuntimeException("ReturnStatus not found: " + statusCode));
        entity.setStatus(newStatus);
        if (resolutionNotes != null) {
            entity.setResolutionNotes(resolutionNotes);
        }

        entity = returnRequestRepository.save(entity);
        return ReturnMapper.toReturnResponse(entity);
    }

    @Transactional
    @RequiresPermission("return:write")
    public void delete(String uuid) {
        ReturnRequest entity = returnRequestRepository.findByUuid(uuid)
                .orElseThrow(() -> new ReturnNotFoundException(uuid));
        returnRequestRepository.delete(entity);
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals("APPROVED") || currentStatus.equals("REJECTED")
                || currentStatus.equals("CLOSED")) {
            throw new InvalidReturnStateException(
                    "Cannot transition from " + currentStatus + " to " + newStatus);
        }
    }
}
