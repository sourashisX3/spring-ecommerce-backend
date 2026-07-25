package com.example.ecommerce_backend.modules.role_user.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.role_user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.role_user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @RequiresPermission("user:read")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<UserResponse> users = userService.getAllUsers(search, active, pageable);
        return ApiResponse.paginated(users.getContent(), "Users retrieved successfully", Pagination.of(users));
    }

    @GetMapping("/{id}")
    @RequiresPermission("user:read")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ApiResponse.success(user, "User retrieved successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ApiResponse.success(user, "User profile fetched successfully");
    }

    @GetMapping("/me/permissions")
    public ResponseEntity<ApiResponse<Map<String, Set<String>>>> getCurrentUserPermissions() {
        Set<String> permissions = userService.getCurrentUserPermissions();
        return ApiResponse.success(Map.of("permissions", permissions), "Permissions fetched successfully");
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        boolean changed = userService.deactivateUser(id);
        String message = changed ? "User deactivated successfully" : "User is already deactivated";
        return ApiResponse.success(null, message);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        boolean changed = userService.activateUser(id);
        String message = changed ? "User activated successfully" : "User is already active";
        return ApiResponse.success(null, message);
    }

    @PostMapping("/me/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateOwnAccount() {
        boolean changed = userService.deactivateOwnAccount();
        String message = changed ? "Account deactivated successfully" : "Account is already deactivated";
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null, "User deleted successfully");
    }
}
