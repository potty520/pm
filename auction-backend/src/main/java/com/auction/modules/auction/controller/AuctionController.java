package com.auction.modules.auction.controller;

import com.auction.common.utils.SecurityUtils;
import com.auction.common.result.Result;
import com.auction.modules.log.service.OpLogService;
import com.auction.modules.system.service.AuthzService;
import com.auction.modules.auction.dto.BidRequest;
import com.auction.modules.auction.dto.AuctionReviewRequest;
import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.auction.entity.BidRecord;
import com.auction.modules.auction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
@Validated
public class AuctionController {

    private final AuctionService auctionService;
    private final AuthzService authzService;
    private final OpLogService opLogService;

    @GetMapping("/list")
    public Result<List<AuctionItem>> list() {
        return Result.ok(auctionService.listActiveItems());
    }

    @GetMapping("/item/{id}")
    public Result<AuctionItem> detail(@PathVariable Long id) {
        AuctionItem item = auctionService.getItemById(id);
        if (item == null) {
            return Result.fail("拍品不存在");
        }
        return Result.ok(item);
    }

    @GetMapping("/search")
    public Result<List<AuctionItem>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status) {
        return Result.ok(auctionService.searchItems(keyword, categoryId, status));
    }

    @PostMapping("/publish")
    public Result<AuctionItem> publish(@Valid @RequestBody AuctionItem item) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        item.setSellerId(currentUserId);
        AuctionItem created = auctionService.publishItem(item);
        opLogService.log(currentUserId, "PUBLISH_ITEM", "AUCTION_ITEM", created.getId(), created.getTitle(), true);
        return Result.ok(created);
    }

    @PostMapping("/bid/{itemId}")
    public Result<BidRecord> bid(@PathVariable Long itemId, @Valid @RequestBody BidRequest request) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        if (request.getUserId() != null && !currentUserId.equals(request.getUserId())) {
            return Result.fail("用户身份不匹配");
        }
        request.setUserId(currentUserId);
        BidRecord record = auctionService.placeBid(itemId, request);
        opLogService.log(currentUserId, "PLACE_BID", "AUCTION_ITEM", itemId, request.getBidAmount().toPlainString(), true);
        return Result.ok(record);
    }

    @GetMapping("/bids/{itemId}")
    public Result<List<BidRecord>> bids(@PathVariable Long itemId) {
        return Result.ok(auctionService.getBidRecords(itemId));
    }

    @PostMapping("/end/{itemId}")
    public Result<Void> end(@PathVariable Long itemId) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        AuctionItem item = auctionService.getItemById(itemId);
        if (item == null) {
            return Result.fail("拍品不存在");
        }
        if (!currentUserId.equals(item.getSellerId())) {
            return Result.fail("仅卖家可结束拍卖");
        }
        auctionService.endAuction(itemId);
        opLogService.log(currentUserId, "END_AUCTION", "AUCTION_ITEM", itemId, "", true);
        return Result.ok(null);
    }

    @PostMapping("/review/{itemId}")
    public Result<AuctionItem> review(@PathVariable Long itemId, @Valid @RequestBody AuctionReviewRequest request) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:item:review")) {
            return Result.fail("无管理员权限");
        }
        if (!request.getApproved() && (request.getRejectReason() == null || request.getRejectReason().trim().isEmpty())) {
            return Result.fail("驳回时请填写原因");
        }
        AuctionItem item = auctionService.reviewItem(itemId, request.getApproved(), request.getRejectReason());
        opLogService.log(uid, "REVIEW_ITEM", "AUCTION_ITEM", itemId, String.valueOf(request.getApproved()), true);
        return Result.ok(item);
    }
}
