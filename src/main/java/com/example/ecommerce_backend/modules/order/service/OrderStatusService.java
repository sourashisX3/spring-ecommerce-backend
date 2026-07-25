package com.example.ecommerce_backend.modules.order.service;

import com.example.ecommerce_backend.modules.order.dto.request.OrderStatusRequest;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusResponse;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusTransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderStatusService {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderStatusTransitionRepository orderStatusTransitionRepository;

    @Transactional(readOnly = true)
    public List<OrderStatusResponse> getAll() {
        return orderStatusRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderStatusResponse getByUuid(String uuid) {
        OrderStatus status = orderStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("OrderStatus not found: " + uuid));
        return toResponse(status);
    }

    @Transactional(readOnly = true)
    public OrderStatusResponse getByCode(String code) {
        OrderStatus status = orderStatusRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("OrderStatus not found: " + code));
        return toResponse(status);
    }

    @Transactional
    public OrderStatusResponse create(OrderStatusRequest request) {
        OrderStatus status = OrderStatus.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder())
                .build();
        status = orderStatusRepository.save(status);
        return toResponse(status);
    }

    @Transactional
    public OrderStatusResponse update(String uuid, OrderStatusRequest request) {
        OrderStatus status = orderStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("OrderStatus not found: " + uuid));
        status.setCode(request.getCode());
        status.setName(request.getName());
        status.setDescription(request.getDescription());
        status.setSortOrder(request.getSortOrder());
        status = orderStatusRepository.save(status);
        return toResponse(status);
    }

    @Transactional
    public OrderStatusResponse toggleStatus(String uuid, boolean active) {
        OrderStatus status = orderStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("OrderStatus not found: " + uuid));
        status.setActive(active);
        status = orderStatusRepository.save(status);
        return toResponse(status);
    }

    @Transactional
    public void delete(String uuid) {
        OrderStatus status = orderStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("OrderStatus not found: " + uuid));
        orderStatusRepository.delete(status);
    }

    @Transactional(readOnly = true)
    public boolean isValidTransition(String fromCode, String toCode, String allowedBy) {
        if (fromCode == null) return true;
        return orderStatusTransitionRepository
                .findByFromStatus_CodeAndToStatus_CodeAndAllowedBy_RoleName(fromCode, toCode, allowedBy)
                .isPresent();
    }

    private OrderStatusResponse toResponse(OrderStatus s) {
        return OrderStatusResponse.builder()
                .id(s.getId())
                .uuid(s.getUuid())
                .code(s.getCode())
                .name(s.getName())
                .description(s.getDescription())
                .sortOrder(s.getSortOrder())
                .isActive(s.isActive())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
