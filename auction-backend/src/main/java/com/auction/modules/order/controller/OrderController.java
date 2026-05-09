package com.auction.modules.order.controller;

import com.auction.common.result.Result;
import com.auction.common.utils.SecurityUtils;
import com.auction.modules.log.service.OpLogService;
import com.auction.modules.order.dto.OrderPayRequest;
import com.auction.modules.order.entity.AuctionOrder;
import com.auction.modules.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;
    private final OpLogService opLogService;

    @GetMapping("/my")
    public Result<List<AuctionOrder>> myOrders() {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        return Result.ok(orderService.listUserOrders(currentUserId));
    }

    @PostMapping("/{orderId}/pay")
    public Result<AuctionOrder> pay(@PathVariable Long orderId, @Valid @RequestBody OrderPayRequest request) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        AuctionOrder order = orderService.payOrder(orderId, currentUserId, request.getPaymentMethod());
        opLogService.log(currentUserId, "PAY_ORDER", "ORDER", orderId, request.getPaymentMethod(), true);
        return Result.ok(order);
    }

    @PostMapping("/{orderId}/ship")
    public Result<AuctionOrder> ship(@PathVariable Long orderId) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        AuctionOrder order = orderService.shipOrder(orderId, currentUserId);
        opLogService.log(currentUserId, "SHIP_ORDER", "ORDER", orderId, "", true);
        return Result.ok(order);
    }

    @PostMapping("/{orderId}/complete")
    public Result<AuctionOrder> complete(@PathVariable Long orderId) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        AuctionOrder order = orderService.completeOrder(orderId, currentUserId);
        opLogService.log(currentUserId, "COMPLETE_ORDER", "ORDER", orderId, "", true);
        return Result.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    public Result<AuctionOrder> cancel(@PathVariable Long orderId) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        AuctionOrder order = orderService.cancelOrder(orderId, currentUserId);
        opLogService.log(currentUserId, "CANCEL_ORDER", "ORDER", orderId, "", true);
        return Result.ok(order);
    }
}
