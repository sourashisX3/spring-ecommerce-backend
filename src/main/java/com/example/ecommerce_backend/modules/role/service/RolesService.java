package com.example.ecommerce_backend.modules.role.service;

import com.example.ecommerce_backend.modules.role.dto.response.RolesResponse;
import com.example.ecommerce_backend.modules.role.mapper.RolesMapper;
import com.example.ecommerce_backend.modules.role.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Transactional(readOnly = true)
    public List<RolesResponse> getAllRoles() {
        return rolesRepository
                .findAll()
                .stream()
                .map(RolesMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

}
