package com.auction.modules.admin.service;

import com.auction.modules.admin.dto.AdminOverviewStats;
import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.order.entity.AuctionOrder;

import java.util.List;

public interface AdminService {
    AdminOverviewStats getOverviewStats();

    List<AuctionItem> listItems(Integer status, Integer limit);

    List<AuctionOrder> listOrders(Integer status, Integer limit);
}
