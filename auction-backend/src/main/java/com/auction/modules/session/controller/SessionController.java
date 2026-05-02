package com.auction.modules.session.controller;

import com.auction.common.result.Result;
import com.auction.modules.session.entity.AuctionSession;
import com.auction.modules.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auction/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/list")
    public Result<List<AuctionSession>> list() {
        return Result.ok(sessionService.listActive());
    }
}
