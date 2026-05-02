package com.auction.modules.session.service;

import com.auction.modules.session.entity.AuctionSession;

import java.util.List;

public interface SessionService {
    List<AuctionSession> listActive();
    List<AuctionSession> listAll();
    AuctionSession create(AuctionSession session);
    AuctionSession update(AuctionSession session);
    void delete(Long id);
}
