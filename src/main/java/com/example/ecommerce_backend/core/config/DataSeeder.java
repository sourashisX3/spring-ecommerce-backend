package com.example.ecommerce_backend.core.config;

import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.permission.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PermissionsRepository permissionsRepository;
    private final RolesRepository rolesRepository;

    public DataSeeder(PermissionsRepository permissionsRepository, RolesRepository rolesRepository) {
        this.permissionsRepository = permissionsRepository;
        this.rolesRepository = rolesRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedPermissions();
        seedRoles();
    }

    private void seedPermissions() {
        Map<String, String> permissions = new LinkedHashMap<>();
        permissions.put("*:*", "Full access to all resources");
        permissions.put("user:read", "View user profiles");
        permissions.put("user:write", "Create, update or delete users");
        permissions.put("role:read", "View roles and their permissions");
        permissions.put("role:write", "Create or delete roles");
        permissions.put("permission:read", "View permissions");
        permissions.put("permission:write", "Create or delete permissions");
        permissions.put("user_permission:read", "View user-level permission overrides");
        permissions.put("user_permission:write", "Assign or remove user-level permission overrides");
        permissions.put("product:read", "View products");
        permissions.put("product:write", "Create, update or delete products");
        permissions.put("category:read", "View categories");
        permissions.put("category:write", "Create, update or delete categories");
        permissions.put("brand:read", "View brands");
        permissions.put("brand:write", "Create, update or delete brands");
        permissions.put("tag:read", "View tags");
        permissions.put("tag:write", "Create, update or delete tags");

        for (Map.Entry<String, String> entry : permissions.entrySet()) {
            permissionsRepository.findByPermissionName(entry.getKey())
                    .orElseGet(() -> {
                        log.info("Seeding permission: {}", entry.getKey());
                        return permissionsRepository.save(
                                Permission.builder()
                                        .permissionName(entry.getKey())
                                        .permissionDescription(entry.getValue())
                                        .build()
                        );
                    });
        }
    }

    private void seedRoles() {
        Permission allPermission = permissionsRepository.findByPermissionName("*:*")
                .orElseThrow(() -> new RuntimeException("*:* permission not found after seeding"));

        if (rolesRepository.findByRoleName("SUPER_ADMIN").isEmpty()) {
            log.info("Seeding SUPER_ADMIN role");
            rolesRepository.save(
                    Role.builder()
                            .roleName("SUPER_ADMIN")
                            .roleDescription("Super admin with full access")
                            .permissions(Set.of(allPermission))
                            .build()
            );
        }

        Permission userRead = permissionsRepository.findByPermissionName("user:read")
                .orElseThrow(() -> new RuntimeException("user:read permission not found after seeding"));

        if (rolesRepository.findByRoleName("USER").isEmpty()) {
            log.info("Seeding USER role");
            rolesRepository.save(
                    Role.builder()
                            .roleName("USER")
                            .roleDescription("Default user role")
                            .permissions(Set.of(userRead))
                            .build()
            );
        }
    }
}
