package com.auction.modules.auction.service.impl;

import com.auction.common.limiter.RateLimiter;
import com.auction.modules.auction.dto.BidRequest;
import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.auction.entity.BidRecord;
import com.auction.modules.auction.mapper.AuctionItemMapper;
import com.auction.modules.auction.mapper.BidRecordMapper;
import com.auction.modules.auction.service.AuctionService;
import com.auction.modules.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionItemMapper auctionItemMapper;
    private final BidRecordMapper bidRecordMapper;
    private final RateLimiter rateLimiter;
    private final OrderService orderService;

    @Override
    public List<AuctionItem> listActiveItems() {
        return auctionItemMapper.selectList(
                new LambdaQueryWrapper<AuctionItem>()
                        .eq(AuctionItem::getStatus, 3)
                        .orderByDesc(AuctionItem::getStartTime)
        );
    }

    @Override
    public AuctionItem getItemById(Long itemId) {
        return auctionItemMapper.selectById(itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionItem publishItem(AuctionItem item) {
        Date now = new Date();
        if (item.getCurrentPrice() == null) {
            item.setCurrentPrice(item.getStartPrice());
        }
        if (item.getBidIncrement() == null) {
            item.setBidIncrement(new BigDecimal("100.00"));
        }
        if (item.getStatus() == null) {
            item.setStatus(0);
        }
        if (item.getStartTime() == null) {
            item.setStartTime(now);
        }
        auctionItemMapper.insert(item);
        return item;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BidRecord placeBid(Long itemId, BidRequest bidRequest) {
        if (!rateLimiter.checkBidRate(bidRequest.getUserId(), itemId)) {
            throw new IllegalStateException("出价过于频繁，请稍后再试");
        }

        AuctionItem item = auctionItemMapper.selectById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("拍品不存在");
        }
        if (item.getStatus() == null || item.getStatus() != 3) {
            throw new IllegalStateException("当前拍品不在拍卖中");
        }

        BigDecimal minBid = item.getCurrentPrice() == null
                ? item.getStartPrice()
                : item.getCurrentPrice().add(item.getBidIncrement());
        if (bidRequest.getBidAmount().compareTo(minBid) < 0) {
            throw new IllegalArgumentException("出价必须大于等于最低可出价金额: " + minBid);
        }

        BidRecord bidRecord = new BidRecord();
        bidRecord.setItemId(itemId);
        bidRecord.setUserId(bidRequest.getUserId());
        bidRecord.setBidAmount(bidRequest.getBidAmount());
        bidRecord.setBidTime(new Date());
        bidRecord.setIsWin(0);
        bidRecordMapper.insert(bidRecord);

        item.setCurrentPrice(bidRequest.getBidAmount());
        auctionItemMapper.updateById(item);
        return bidRecord;
    }

    @Override
    public List<BidRecord> getBidRecords(Long itemId) {
        return bidRecordMapper.selectList(
                new LambdaQueryWrapper<BidRecord>()
                        .eq(BidRecord::getItemId, itemId)
                        .orderByDesc(BidRecord::getBidAmount)
                        .orderByDesc(BidRecord::getBidTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endAuction(Long itemId) {
        AuctionItem item = auctionItemMapper.selectById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("拍品不存在");
        }
        if (item.getStatus() == null || item.getStatus() != 3) {
            throw new IllegalStateException("当前拍品不在拍卖中");
        }

        BidRecord highestBid = bidRecordMapper.selectOne(
                new LambdaQueryWrapper<BidRecord>()
                        .eq(BidRecord::getItemId, itemId)
                        .orderByDesc(BidRecord::getBidAmount)
                        .orderByDesc(BidRecord::getBidTime)
                        .last("LIMIT 1")
        );

        if (highestBid == null) {
            item.setStatus(5);
            auctionItemMapper.updateById(item);
            return;
        }

        highestBid.setIsWin(1);
        bidRecordMapper.updateById(highestBid);
        item.setStatus(4);
        item.setCurrentPrice(highestBid.getBidAmount());
        auctionItemMapper.updateById(item);
        orderService.createOrderFromAuction(item, highestBid);
    }

    @Override
    public List<AuctionItem> searchItems(String keyword, Integer categoryId, Integer status) {
        LambdaQueryWrapper<AuctionItem> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(AuctionItem::getTitle, keyword).or().like(AuctionItem::getDescription, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(AuctionItem::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(AuctionItem::getStatus, status);
        }
        wrapper.orderByDesc(AuctionItem::getId);
        return auctionItemMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionItem reviewItem(Long itemId, boolean approved, String rejectReason) {
        AuctionItem item = auctionItemMapper.selectById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("拍品不存在");
        }
        if (item.getStatus() == null || item.getStatus() != 0) {
            throw new IllegalStateException("当前拍品不在待审核状态");
        }
        if (approved) {
            item.setStatus(1);
            item.setRejectReason(null);
        } else {
            item.setStatus(2);
            item.setRejectReason(rejectReason);
        }
        auctionItemMapper.updateById(item);
        return item;
    }
}
