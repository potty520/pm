package com.auction.modules.order.service.impl;

import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.auction.entity.BidRecord;
import com.auction.modules.order.entity.AuctionOrder;
import com.auction.modules.order.mapper.AuctionOrderMapper;
import com.auction.modules.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AuctionOrderMapper auctionOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionOrder createOrderFromAuction(AuctionItem item, BidRecord winningBid) {
        AuctionOrder existing = auctionOrderMapper.selectOne(
                new LambdaQueryWrapper<AuctionOrder>().eq(AuctionOrder::getItemId, item.getId()).last("LIMIT 1")
        );
        if (existing != null) {
            return existing;
        }

        AuctionOrder order = new AuctionOrder();
        order.setOrderNo(generateOrderNo());
        order.setItemId(item.getId());
        order.setBuyerId(winningBid.getUserId());
        order.setSellerId(item.getSellerId());
        order.setFinalPrice(winningBid.getBidAmount());
        order.setStatus(0);
        auctionOrderMapper.insert(order);
        return order;
    }

    @Override
    public List<AuctionOrder> listUserOrders(Long userId) {
        return auctionOrderMapper.selectList(
                new LambdaQueryWrapper<AuctionOrder>()
                        .and(wrapper -> wrapper.eq(AuctionOrder::getBuyerId, userId)
                                .or()
                                .eq(AuctionOrder::getSellerId, userId))
                        .orderByDesc(AuctionOrder::getCreatedAt)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionOrder payOrder(Long orderId, Long userId, String paymentMethod) {
        AuctionOrder order = mustGetOrder(orderId);
        if (!userId.equals(order.getBuyerId())) {
            throw new IllegalStateException("仅买家可支付订单");
        }
        if (order.getStatus() != 0) {
            throw new IllegalStateException("当前订单状态不可支付");
        }
        order.setStatus(1);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTime(new Date());
        auctionOrderMapper.updateById(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionOrder shipOrder(Long orderId, Long userId) {
        AuctionOrder order = mustGetOrder(orderId);
        if (!userId.equals(order.getSellerId())) {
            throw new IllegalStateException("仅卖家可发货");
        }
        if (order.getStatus() != 1) {
            throw new IllegalStateException("当前订单状态不可发货");
        }
        order.setStatus(2);
        auctionOrderMapper.updateById(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionOrder completeOrder(Long orderId, Long userId) {
        AuctionOrder order = mustGetOrder(orderId);
        if (!userId.equals(order.getBuyerId())) {
            throw new IllegalStateException("仅买家可确认收货");
        }
        if (order.getStatus() != 2) {
            throw new IllegalStateException("当前订单状态不可完成");
        }
        order.setStatus(3);
        auctionOrderMapper.updateById(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionOrder cancelOrder(Long orderId, Long userId) {
        AuctionOrder order = mustGetOrder(orderId);
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            throw new IllegalStateException("仅交易双方可取消订单");
        }
        if (order.getStatus() != 0) {
            throw new IllegalStateException("仅待支付订单可取消");
        }
        order.setStatus(4);
        auctionOrderMapper.updateById(order);
        return order;
    }

    private AuctionOrder mustGetOrder(Long orderId) {
        AuctionOrder order = auctionOrderMapper.selectById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        return order;
    }

    private String generateOrderNo() {
        String prefix = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
        int suffix = 1000 + new Random().nextInt(9000);
        return prefix + suffix;
    }
}
