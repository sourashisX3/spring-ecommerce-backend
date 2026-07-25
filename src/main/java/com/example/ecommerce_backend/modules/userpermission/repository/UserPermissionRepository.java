package com.example.ecommerce_backend.modules.userpermission.repository;

import com.example.ecommerce_backend.modules.userpermission.entity.UserPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    List<UserPermission> findByUserId(Long userId);

    Page<UserPermission> findByUserId(Long userId, Pageable pageable);

    Optional<UserPermission> findByUserIdAndPermissionId(Long userId, Long permissionId);
}
