package com.example.ecommerce_backend.modules.home.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.home.dto.DashboardResponse;
import com.example.ecommerce_backend.modules.home.dto.HomeResponse;
import com.example.ecommerce_backend.modules.home.service.HomeService;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHome() {
        HomeResponse home = homeService.getHomeData();
        return ApiResponse.success(home, "Home data retrieved successfully");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal User user) {
        DashboardResponse dashboard = homeService.getDashboard(user);
        return ApiResponse.success(dashboard, "Dashboard data retrieved successfully");
    }
}
