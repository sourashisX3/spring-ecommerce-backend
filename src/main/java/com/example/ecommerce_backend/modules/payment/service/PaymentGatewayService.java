package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.payment.dto.request.PaymentGatewayRequest;
import com.example.ecommerce_backend.modules.payment.dto.response.PaymentGatewayResponse;
import com.example.ecommerce_backend.modules.payment.entity.PaymentGateway;
import com.example.ecommerce_backend.modules.payment.exception.PaymentGatewayNotFoundException;
import com.example.ecommerce_backend.modules.payment.mapper.PaymentMapper;
import com.example.ecommerce_backend.modules.payment.repository.PaymentGatewayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentGatewayService {

    @Autowired
    private PaymentGatewayRepository paymentGatewayRepository;

    @Transactional(readOnly = true)
    public List<PaymentGatewayResponse> getAll() {
        return paymentGatewayRepository.findAll().stream()
                .map(PaymentMapper::toGatewayResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentGatewayResponse getByUuid(String uuid) {
        PaymentGateway gateway = paymentGatewayRepository.findByUuid(uuid)
                .orElseThrow(() -> new PaymentGatewayNotFoundException(uuid));
        return PaymentMapper.toGatewayResponse(gateway);
    }

    @Transactional(readOnly = true)
    public PaymentGatewayResponse getByCode(String code) {
        PaymentGateway gateway = paymentGatewayRepository.findByCode(code)
                .orElseThrow(() -> new PaymentGatewayNotFoundException(code));
        return PaymentMapper.toGatewayResponse(gateway);
    }

    @Transactional
    @RequiresPermission("payment:write")
    public PaymentGatewayResponse create(PaymentGatewayRequest request) {
        PaymentGateway gateway = PaymentGateway.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .configTemplate(request.getConfigTemplate())
                .build();
        gateway = paymentGatewayRepository.save(gateway);
        return PaymentMapper.toGatewayResponse(gateway);
    }

    @Transactional
    @RequiresPermission("payment:write")
    public PaymentGatewayResponse update(String uuid, PaymentGatewayRequest request) {
        PaymentGateway gateway = paymentGatewayRepository.findByUuid(uuid)
                .orElseThrow(() -> new PaymentGatewayNotFoundException(uuid));
        gateway.setCode(request.getCode());
        gateway.setName(request.getName());
        gateway.setDescription(request.getDescription());
        gateway.setConfigTemplate(request.getConfigTemplate());
        gateway = paymentGatewayRepository.save(gateway);
        return PaymentMapper.toGatewayResponse(gateway);
    }

    @Transactional
    @RequiresPermission("payment:write")
    public void toggleStatus(String uuid, boolean active) {
        PaymentGateway gateway = paymentGatewayRepository.findByUuid(uuid)
                .orElseThrow(() -> new PaymentGatewayNotFoundException(uuid));
        gateway.setActive(active);
        paymentGatewayRepository.save(gateway);
    }

    @Transactional
    @RequiresPermission("payment:write")
    public void delete(String uuid) {
        PaymentGateway gateway = paymentGatewayRepository.findByUuid(uuid)
                .orElseThrow(() -> new PaymentGatewayNotFoundException(uuid));
        paymentGatewayRepository.delete(gateway);
    }
}
