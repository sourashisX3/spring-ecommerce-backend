package com.example.ecommerce_backend.modules.returns.repository;

import com.example.ecommerce_backend.modules.returns.entity.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    Optional<ReturnRequest> findByUuid(String uuid);
    List<ReturnRequest> findByUserId(Long userId);

    Page<ReturnRequest> findByUserId(Long userId, Pageable pageable);

    List<ReturnRequest> findByOrderId(Long orderId);
    List<ReturnRequest> findByStatus_Code(String statusCode);
}
