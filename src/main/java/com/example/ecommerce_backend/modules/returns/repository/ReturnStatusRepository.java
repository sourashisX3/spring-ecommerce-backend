package com.example.ecommerce_backend.modules.returns.repository;

import com.example.ecommerce_backend.modules.returns.entity.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnStatusRepository extends JpaRepository<ReturnStatus, Long> {

    Optional<ReturnStatus> findByUuid(String uuid);

    Optional<ReturnStatus> findByCode(String code);
}
