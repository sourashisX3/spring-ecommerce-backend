package com.example.ecommerce_backend.modules.returns.repository;

import com.example.ecommerce_backend.modules.returns.entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnItemRepository extends JpaRepository<ReturnItem, Long> {
    List<ReturnItem> findByReturnRequestId(Long returnRequestId);
}
