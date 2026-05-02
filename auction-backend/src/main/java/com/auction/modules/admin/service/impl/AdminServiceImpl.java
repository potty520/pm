package com.auction.modules.admin.service.impl;

import com.auction.modules.admin.dto.AdminOverviewStats;
import com.auction.modules.admin.service.AdminService;
import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.auction.mapper.AuctionItemMapper;
import com.auction.modules.order.entity.AuctionOrder;
import com.auction.modules.order.mapper.AuctionOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AuctionItemMapper auctionItemMapper;
    private final AuctionOrderMapper auctionOrderMapper;

    @Override
    public AdminOverviewStats getOverviewStats() {
        AdminOverviewStats stats = new AdminOverviewStats();
        stats.setTotalItems(auctionItemMapper.selectCount(null));
        stats.setActiveItems(countItemsByStatus(3));
        stats.setSoldItems(countItemsByStatus(4));
        stats.setUnsoldItems(countItemsByStatus(5));
        stats.setTotalOrders(auctionOrderMapper.selectCount(null));
        stats.setPaidOrders(countOrdersByStatus(1));
        stats.setCompletedOrders(countOrdersByStatus(3));
        stats.setPaidAmount(sumAmountByStatuses(1, 2, 3));
        stats.setCompletedAmount(sumAmountByStatuses(3));
        return stats;
    }

    @Override
    public List<AuctionItem> listItems(Integer status, Integer limit) {
        int safeLimit = normalizeLimit(limit);
        LambdaQueryWrapper<AuctionItem> wrapper = new LambdaQueryWrapper<AuctionItem>()
                .orderByDesc(AuctionItem::getId)
                .last("LIMIT " + safeLimit);
        if (status != null) {
            wrapper.eq(AuctionItem::getStatus, status);
        }
        List<AuctionItem> items = auctionItemMapper.selectList(wrapper);
        return items == null ? Collections.emptyList() : items;
    }

    @Override
    public List<AuctionOrder> listOrders(Integer status, Integer limit) {
        int safeLimit = normalizeLimit(limit);
        LambdaQueryWrapper<AuctionOrder> wrapper = new LambdaQueryWrapper<AuctionOrder>()
                .orderByDesc(AuctionOrder::getId)
                .last("LIMIT " + safeLimit);
        if (status != null) {
            wrapper.eq(AuctionOrder::getStatus, status);
        }
        List<AuctionOrder> orders = auctionOrderMapper.selectList(wrapper);
        return orders == null ? Collections.emptyList() : orders;
    }

    private Long countItemsByStatus(Integer status) {
        return auctionItemMapper.selectCount(
                new LambdaQueryWrapper<AuctionItem>().eq(AuctionItem::getStatus, status)
        );
    }

    private Long countOrdersByStatus(Integer status) {
        return auctionOrderMapper.selectCount(
                new LambdaQueryWrapper<AuctionOrder>().eq(AuctionOrder::getStatus, status)
        );
    }

    private BigDecimal sumAmountByStatuses(Integer... statuses) {
        BigDecimal total = BigDecimal.ZERO;
        for (Integer status : statuses) {
            List<AuctionOrder> rows = auctionOrderMapper.selectList(
                    new LambdaQueryWrapper<AuctionOrder>()
                            .eq(AuctionOrder::getStatus, status)
                            .select(AuctionOrder::getFinalPrice)
            );
            for (AuctionOrder row : rows) {
                if (row.getFinalPrice() != null) {
                    total = total.add(row.getFinalPrice());
                }
            }
        }
        return total;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 20;
        }
        return Math.min(limit, 200);
    }
}
