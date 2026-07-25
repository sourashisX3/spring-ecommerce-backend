package com.example.ecommerce_backend.modules.role.repository;

import com.example.ecommerce_backend.modules.role.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);

    @Query("""
            SELECT r FROM Role r
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(r.roleName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(r.roleDescription) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Role> findBySearchTerm(@Param("search") String search, Pageable pageable);
}
