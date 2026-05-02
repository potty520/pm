package com.auction.modules.auction.controller;

import com.auction.modules.auction.dto.BidRequest;
import com.auction.modules.auction.entity.BidRecord;
import com.auction.modules.auction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BidWsController {

    private final AuctionService auctionService;

    @MessageMapping("/bid/{itemId}")
    @SendTo("/topic/bid/{itemId}")
    public BidRecord bid(@DestinationVariable Long itemId, @Valid @Payload BidRequest request) {
        try {
            return auctionService.placeBid(itemId, request);
        } catch (Exception ex) {
            log.error("WebSocket bid failed, itemId={}", itemId, ex);
            throw ex;
        }
    }
}
