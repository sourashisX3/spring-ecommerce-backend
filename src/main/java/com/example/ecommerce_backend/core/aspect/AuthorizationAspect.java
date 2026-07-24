package com.example.ecommerce_backend.core.aspect;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import com.example.ecommerce_backend.modules.role_user.exception.PermissionRequiredException;
import com.example.ecommerce_backend.modules.role_user.repository.UserRepository;
import com.example.ecommerce_backend.modules.role_user.service.PermissionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationAspect {

    private final PermissionService permissionService;
    private final UserRepository userRepository;

    public AuthorizationAspect(PermissionService permissionService, UserRepository userRepository) {
        this.permissionService = permissionService;
        this.userRepository = userRepository;
    }

    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new PermissionRequiredException("Authentication required");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PermissionRequiredException("Authenticated user not found"));

        if (!permissionService.hasPermission(user, requiresPermission.value())) {
            throw new PermissionRequiredException("Access denied. Required permission: " + requiresPermission.value());
        }

        return joinPoint.proceed();
    }
}
