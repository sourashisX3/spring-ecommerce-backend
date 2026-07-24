package com.example.ecommerce_backend.modules.role_user.repository;

import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);

    Optional<User> findByEmail(String email);
}
