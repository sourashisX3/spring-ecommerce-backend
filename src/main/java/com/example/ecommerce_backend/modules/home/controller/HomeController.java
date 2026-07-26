package com.example.ecommerce_backend.modules.home.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.home.dto.DashboardResponse;
import com.example.ecommerce_backend.modules.home.dto.HomeResponse;
import com.example.ecommerce_backend.modules.home.service.HomeService;
import com.example.ecommerce_backend.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@Tag(name = "Home", description = "Home API")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Operation(summary = "Get home page data", description = "Retrieves home page data including categories, brands, and products")
    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHome() {
        HomeResponse home = homeService.getHomeData();
        return ApiResponse.success(home, "Home data retrieved successfully");
    }

    @Operation(summary = "Get dashboard data", description = "Retrieves dashboard data for the authenticated user")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal User user) {
        DashboardResponse dashboard = homeService.getDashboard(user);
        return ApiResponse.success(dashboard, "Dashboard data retrieved successfully");
    }
}
