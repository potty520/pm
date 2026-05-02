package com.auction.modules.admin.controller;

import com.auction.common.result.Result;
import com.auction.common.utils.SecurityUtils;
import com.auction.modules.admin.dto.AdminOverviewStats;
import com.auction.modules.admin.service.AdminService;
import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.category.entity.AuctionCategory;
import com.auction.modules.category.service.CategoryService;
import com.auction.modules.log.service.OpLogService;
import com.auction.modules.log.entity.OpLog;
import com.auction.modules.order.entity.AuctionOrder;
import com.auction.modules.session.entity.AuctionSession;
import com.auction.modules.session.service.SessionService;
import com.auction.modules.system.service.AuthzService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;
    private final SessionService sessionService;
    private final AuthzService authzService;
    private final OpLogService opLogService;

    @GetMapping("/stats/overview")
    public Result<AdminOverviewStats> overview() {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:stats")) {
            return Result.fail("无管理员权限");
        }
        return Result.ok(adminService.getOverviewStats());
    }

    @GetMapping("/items")
    public Result<List<AuctionItem>> items(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer limit) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:item:list")) {
            return Result.fail("无管理员权限");
        }
        return Result.ok(adminService.listItems(status, limit));
    }

    @GetMapping("/orders")
    public Result<List<AuctionOrder>> orders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer limit) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:order:list")) {
            return Result.fail("无管理员权限");
        }
        return Result.ok(adminService.listOrders(status, limit));
    }

    @GetMapping("/categories")
    public Result<List<AuctionCategory>> categories() {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:category:manage")) return Result.fail("无管理员权限");
        return Result.ok(categoryService.listAll());
    }

    @PostMapping("/categories")
    public Result<AuctionCategory> createCategory(@RequestBody AuctionCategory category) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:category:manage")) return Result.fail("无管理员权限");
        AuctionCategory data = categoryService.create(category);
        opLogService.log(uid, "CREATE_CATEGORY", "CATEGORY", data.getId(), data.getName(), true);
        return Result.ok(data);
    }

    @PutMapping("/categories")
    public Result<AuctionCategory> updateCategory(@RequestBody AuctionCategory category) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:category:manage")) return Result.fail("无管理员权限");
        AuctionCategory data = categoryService.update(category);
        opLogService.log(uid, "UPDATE_CATEGORY", "CATEGORY", data.getId(), data.getName(), true);
        return Result.ok(data);
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:category:manage")) return Result.fail("无管理员权限");
        categoryService.delete(id);
        opLogService.log(uid, "DELETE_CATEGORY", "CATEGORY", id, "", true);
        return Result.ok(null);
    }

    @GetMapping("/sessions")
    public Result<List<AuctionSession>> sessions() {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:session:manage")) return Result.fail("无管理员权限");
        return Result.ok(sessionService.listAll());
    }

    @PostMapping("/sessions")
    public Result<AuctionSession> createSession(@RequestBody AuctionSession session) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:session:manage")) return Result.fail("无管理员权限");
        AuctionSession data = sessionService.create(session);
        opLogService.log(uid, "CREATE_SESSION", "SESSION", data.getId(), data.getName(), true);
        return Result.ok(data);
    }

    @PutMapping("/sessions")
    public Result<AuctionSession> updateSession(@RequestBody AuctionSession session) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:session:manage")) return Result.fail("无管理员权限");
        AuctionSession data = sessionService.update(session);
        opLogService.log(uid, "UPDATE_SESSION", "SESSION", data.getId(), data.getName(), true);
        return Result.ok(data);
    }

    @DeleteMapping("/sessions/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:session:manage")) return Result.fail("无管理员权限");
        sessionService.delete(id);
        opLogService.log(uid, "DELETE_SESSION", "SESSION", id, "", true);
        return Result.ok(null);
    }

    @GetMapping("/logs")
    public Result<List<OpLog>> logs(@RequestParam(required = false) Integer limit) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:log:view")) return Result.fail("无管理员权限");
        return Result.ok(opLogService.listRecent(limit));
    }
}
