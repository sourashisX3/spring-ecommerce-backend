package com.example.ecommerce_backend.core.aspect;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.permission.exception.PermissionRequiredException;
import com.example.ecommerce_backend.modules.permission.service.PermissionService;
import com.example.ecommerce_backend.modules.user.entity.User;
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

    public AuthorizationAspect(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new PermissionRequiredException("Authentication required");
        }

        User user = (User) authentication.getPrincipal();

        if (!permissionService.hasPermission(user, requiresPermission.value())) {
            throw new PermissionRequiredException("Access denied. Required permission: " + requiresPermission.value());
        }

        return joinPoint.proceed();
    }
}
