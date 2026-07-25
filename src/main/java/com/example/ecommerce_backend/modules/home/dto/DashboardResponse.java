package com.example.ecommerce_backend.modules.home.dto;

import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private long orderCount;
    private long wishlistCount;
    private long cartCount;
    private BigDecimal walletBalance;
    private long unreadNotificationCount;
    private List<OrderResponse> recentOrders;
}
