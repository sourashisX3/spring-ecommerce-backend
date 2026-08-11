package com.example.ecommerce_backend.modules.payment.repository;

import com.example.ecommerce_backend.modules.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByUuid(String uuid);
    List<Refund> findByPaymentId(Long paymentId);
    List<Refund> findByPaymentIdAndStatus_Code(Long paymentId, String code);
    List<Refund> findByReturnRequestId(Long returnRequestId);
}
