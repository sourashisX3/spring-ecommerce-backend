package com.example.ecommerce_backend.core.config;

import com.example.ecommerce_backend.modules.role_user.entity.Permission;
import com.example.ecommerce_backend.modules.role_user.entity.Role;
import com.example.ecommerce_backend.modules.role_user.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role_user.repository.RolesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
    public void run(ApplicationArguments args) {
        Permission allPermission = permissionsRepository.findByPermissionName("*:*")
                .orElseGet(() -> {
                    log.info("Seeding *:* permission");
                    return permissionsRepository.save(
                            Permission.builder().permissionName("*:*").permissionDescription("Full access to all resources").build()
                    );
                });

        if (!rolesRepository.findByRoleName("SUPER_ADMIN").isPresent()) {
            log.info("Seeding SUPER_ADMIN role");
            rolesRepository.save(
                    Role.builder()
                            .roleName("SUPER_ADMIN")
                            .roleDescription("Super admin with full access")
                            .permissions(Set.of(allPermission))
                            .build()
            );
        }
    }
}
