package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.role_user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import com.example.ecommerce_backend.modules.role_user.entity.UserAddress;
import com.example.ecommerce_backend.modules.role_user.exception.AccountDeactivatedException;
import com.example.ecommerce_backend.modules.role_user.exception.CannotDeleteProtectedRoleException;
import com.example.ecommerce_backend.modules.role_user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.role_user.mapper.UserMapper;
import com.example.ecommerce_backend.modules.role_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toUserResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(0L));
        return UserMapper.toUserResponse(user);
    }

    @Transactional
    @RequiresPermission("user:write")
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if ("SUPER_ADMIN".equals(user.getRole().getRoleName())) {
            throw new CannotDeleteProtectedRoleException(user.getRole().getRoleName());
        }
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    @RequiresPermission("user:write")
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateOwnAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(0L));
        if ("SUPER_ADMIN".equals(user.getRole().getRoleName())) {
            throw new CannotDeleteProtectedRoleException(user.getRole().getRoleName());
        }
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    @RequiresPermission("user:write")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}
