package com.auction.modules.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminOverviewStats {
    private Long totalItems;
    private Long activeItems;
    private Long soldItems;
    private Long unsoldItems;
    private Long totalOrders;
    private Long paidOrders;
    private Long completedOrders;
    private BigDecimal paidAmount;
    private BigDecimal completedAmount;
}
