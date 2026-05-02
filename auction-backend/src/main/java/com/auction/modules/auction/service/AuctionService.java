package com.auction.modules.auction.service;

import com.auction.modules.auction.dto.BidRequest;
import com.auction.modules.auction.entity.AuctionItem;
import com.auction.modules.auction.entity.BidRecord;

import java.util.List;

public interface AuctionService {
    List<AuctionItem> listActiveItems();

    AuctionItem getItemById(Long itemId);

    AuctionItem publishItem(AuctionItem item);

    BidRecord placeBid(Long itemId, BidRequest bidRequest);

    List<BidRecord> getBidRecords(Long itemId);

    void endAuction(Long itemId);

    List<AuctionItem> searchItems(String keyword, Integer categoryId, Integer status);

    AuctionItem reviewItem(Long itemId, boolean approved, String rejectReason);
}
