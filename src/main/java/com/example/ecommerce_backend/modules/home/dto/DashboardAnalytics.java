package com.example.ecommerce_backend.modules.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Store-wide analytics for the admin dashboard")
public class DashboardAnalytics {

    @Schema(description = "Total store revenue from non-cancelled, non-refunded orders", example = "18420.50")
    private BigDecimal totalRevenue;

    @Schema(description = "Total number of orders in the store", example = "42")
    private long totalOrders;

    @Schema(description = "Total number of registered customers", example = "126")
    private long totalCustomers;

    @Schema(description = "Number of variants with low or zero stock", example = "3")
    private long lowStockVariants;

    @Schema(description = "Daily revenue for the last 7 days")
    private List<RevenuePoint> revenueTrend;

    @Schema(description = "Number of orders grouped by status")
    private List<StatusCount> orderStatusBreakdown;

    @Schema(description = "Top 5 best-selling products by quantity sold")
    private List<TopProduct> topProducts;

    @Schema(description = "New customer registrations per day for the last 7 days")
    private List<GrowthPoint> userGrowth;

    @Schema(description = "Variants with low or zero stock")
    private List<LowStockItem> lowStockItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenuePoint {
        private LocalDate date;
        private BigDecimal amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusCount {
        private String status;
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private Long productId;
        private String productName;
        private String sku;
        private long quantitySold;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthPoint {
        private LocalDate date;
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockItem {
        private Long productId;
        private String productName;
        private String sku;
        private String variantName;
        private int stock;
    }
}