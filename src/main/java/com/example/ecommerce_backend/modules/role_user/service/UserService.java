package com.example.ecommerce_backend.modules.role_user.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.service.RefreshTokenService;
import com.example.ecommerce_backend.modules.role_user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import com.example.ecommerce_backend.modules.role_user.exception.AuthenticationRequiredException;
import com.example.ecommerce_backend.modules.role_user.exception.CannotDeactivateProtectedUserException;
import com.example.ecommerce_backend.modules.role_user.exception.CannotDeleteProtectedRoleException;
import com.example.ecommerce_backend.modules.role_user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.role_user.mapper.UserMapper;
import com.example.ecommerce_backend.modules.role_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String search, Boolean active, Pageable pageable) {
        return userRepository.findBySearchTerm(search, active, pageable)
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
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return UserMapper.toUserResponse(user);
    }

    public UserResponse getCurrentUser() {
        User user = getCurrentUserEntity();
        return UserMapper.toUserResponse(user);
    }

    public Set<String> getCurrentUserPermissions() {
        User user = getCurrentUserEntity();
        return permissionService.getEffectivePermissions(user);
    }

    private User getCurrentUserEntity() {
        String email = getAuthenticatedEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Transactional
    @RequiresPermission("user:write")
    public boolean deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if ("SUPER_ADMIN".equals(user.getRole().getRoleName())) {
            throw new CannotDeactivateProtectedUserException();
        }
        if (!user.isActive()) {
            return false;
        }
        user.setActive(false);
        userRepository.save(user);
        refreshTokenService.revokeAllUserTokens(user.getId());
        return true;
    }

    @Transactional
    @RequiresPermission("user:write")
    public boolean activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (user.isActive()) {
            return false;
        }
        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean deactivateOwnAccount() {
        String email = getAuthenticatedEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        if ("SUPER_ADMIN".equals(user.getRole().getRoleName())) {
            throw new CannotDeactivateProtectedUserException();
        }
        if (!user.isActive()) {
            return false;
        }
        user.setActive(false);
        userRepository.save(user);
        refreshTokenService.revokeAllUserTokens(user.getId());
        return true;
    }

    @Transactional
    @RequiresPermission("user:write")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if ("SUPER_ADMIN".equals(user.getRole().getRoleName())) {
            throw new CannotDeleteProtectedRoleException(user.getRole().getRoleName());
        }
        userRepository.delete(user);
    }

    private String getAuthenticatedEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthenticationRequiredException();
        }
        return auth.getName();
    }
}
