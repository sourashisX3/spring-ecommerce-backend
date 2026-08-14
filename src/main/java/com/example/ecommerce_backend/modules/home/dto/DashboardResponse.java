package com.example.ecommerce_backend.modules.home.dto;

import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response containing dashboard summary data")
public class DashboardResponse {
    @Schema(description = "Total number of orders", example = "15")
    private long orderCount;
    @Schema(description = "Total number of wishlist items", example = "8")
    private long wishlistCount;
    @Schema(description = "Total number of cart items", example = "3")
    private long cartCount;
    @Schema(description = "Current wallet balance", example = "250.00")
    private BigDecimal walletBalance;
    @Schema(description = "ISO 4217 code of the currency the wallet balance is denominated in", example = "INR")
    private String walletCurrency;
    @Schema(description = "Number of unread notifications", example = "5")
    private long unreadNotificationCount;
    @Schema(description = "List of recent orders")
    private List<OrderResponse> recentOrders;
    @Schema(description = "Store-wide analytics; present only for privileged roles")
    private DashboardAnalytics analytics;
}
