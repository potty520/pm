package com.auction.modules.category.service;

import com.auction.modules.category.entity.AuctionCategory;

import java.util.List;

public interface CategoryService {
    List<AuctionCategory> listActive();
    List<AuctionCategory> listAll();
    AuctionCategory create(AuctionCategory category);
    AuctionCategory update(AuctionCategory category);
    void delete(Long id);
}
