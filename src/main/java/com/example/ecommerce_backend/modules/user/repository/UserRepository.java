package com.example.ecommerce_backend.modules.user.repository;

import com.example.ecommerce_backend.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByRole_RoleNameIn(List<String> roleNames);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.role r
            LEFT JOIN FETCH r.permissions
            LEFT JOIN FETCH u.userPermissions up
            LEFT JOIN FETCH up.permission
            WHERE u.email = :email
            """)
    Optional<User> findByEmailWithPermissions(@Param("email") String email);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.role r
            LEFT JOIN FETCH r.permissions
            LEFT JOIN FETCH u.userPermissions up
            LEFT JOIN FETCH up.permission
            WHERE u.phoneNumber = :phoneNumber
            """)
    Optional<User> findByPhoneNumberWithPermissions(@Param("phoneNumber") String phoneNumber);

    @Query("""
            SELECT u FROM User u
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR u.phoneNumber LIKE CONCAT('%', :search, '%'))
            AND (:active IS NULL OR u.isActive = :active)
            """)
    Page<User> findBySearchTerm(@Param("search") String search,
                                @Param("active") Boolean active,
                                Pageable pageable);
}
