package com.example.ecommerce_backend.modules.role_user.repository;

import com.example.ecommerce_backend.modules.role_user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);

}
