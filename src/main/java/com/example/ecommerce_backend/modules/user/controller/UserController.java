package com.example.ecommerce_backend.modules.user.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.user.dto.request.ChangePasswordRequest;
import com.example.ecommerce_backend.modules.user.dto.request.UpdateProfileRequest;
import com.example.ecommerce_backend.modules.user.dto.response.UserResponse;
import com.example.ecommerce_backend.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @RequiresPermission("user:read")
    @Operation(summary = "Get all users", description = "Retrieves all users with optional search and filtering")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<UserResponse> users = userService.getAllUsers(search, active, pageable);
        return ApiResponse.paginated(users.getContent(), "Users retrieved successfully", Pagination.of(users));
    }

    @GetMapping("/{id}")
    @RequiresPermission("user:read")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ApiResponse.success(user, "User retrieved successfully");
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ApiResponse.success(user, "User profile fetched successfully");
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile", description = "Updates the currently authenticated user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse user = userService.updateProfile(request);
        return ApiResponse.success(user, "Profile updated successfully");
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password", description = "Changes the password of the currently authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.success(null, "Password changed successfully");
    }

    @GetMapping("/me/permissions")
    @Operation(summary = "Get current user permissions", description = "Retrieves the permissions of the currently authenticated user")
    public ResponseEntity<ApiResponse<Map<String, Set<String>>>> getCurrentUserPermissions() {
        Set<String> permissions = userService.getCurrentUserPermissions();
        return ApiResponse.success(Map.of("permissions", permissions), "Permissions fetched successfully");
    }

    @PatchMapping("/{id}/deactivate")
    @RequiresPermission("user:write")
    @Operation(summary = "Deactivate user", description = "Deactivates a user account by ID")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        boolean changed = userService.deactivateUser(id);
        String message = changed ? "User deactivated successfully" : "User is already deactivated";
        return ApiResponse.success(null, message);
    }

    @PatchMapping("/{id}/activate")
    @RequiresPermission("user:write")
    @Operation(summary = "Activate user", description = "Activates a user account by ID")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        boolean changed = userService.activateUser(id);
        String message = changed ? "User activated successfully" : "User is already active";
        return ApiResponse.success(null, message);
    }

    @PostMapping("/me/deactivate")
    @Operation(summary = "Deactivate own account", description = "Deactivates the currently authenticated user's own account")
    public ResponseEntity<ApiResponse<Void>> deactivateOwnAccount() {
        boolean changed = userService.deactivateOwnAccount();
        String message = changed ? "Account deactivated successfully" : "Account is already deactivated";
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("user:write")
    @Operation(summary = "Delete user", description = "Deletes a user account by ID")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null, "User deleted successfully");
    }
}
