package com.example.ecommerce_backend.modules.role.repository;

import com.example.ecommerce_backend.modules.role.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionsRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String permissionName);
    List<Permission> findByResource(String resource);

}
