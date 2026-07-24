package com.example.ecommerce_backend.modules.role_user.repository;

import com.example.ecommerce_backend.modules.role_user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionsRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermissionName(String permissionName);

}
