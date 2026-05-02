package com.auction.modules.order.service;

import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.auction.entity.BidRecord;
import com.auction.modules.order.entity.AuctionOrder;

import java.util.List;

public interface OrderService {
    AuctionOrder createOrderFromAuction(AuctionItem item, BidRecord winningBid);

    List<AuctionOrder> listUserOrders(Long userId);

    AuctionOrder payOrder(Long orderId, Long userId, String paymentMethod);

    AuctionOrder shipOrder(Long orderId, Long userId);

    AuctionOrder completeOrder(Long orderId, Long userId);

    AuctionOrder cancelOrder(Long orderId, Long userId);
}
