package com.auction.modules.session.service.impl;

import com.auction.modules.session.entity.AuctionSession;
import com.auction.modules.session.mapper.AuctionSessionMapper;
import com.auction.modules.session.service.SessionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final AuctionSessionMapper sessionMapper;

    @Override
    public List<AuctionSession> listActive() {
        return sessionMapper.selectList(new LambdaQueryWrapper<AuctionSession>()
                .eq(AuctionSession::getStatus, 1).orderByDesc(AuctionSession::getStartTime));
    }

    @Override
    public List<AuctionSession> listAll() {
        return sessionMapper.selectList(new LambdaQueryWrapper<AuctionSession>().orderByDesc(AuctionSession::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionSession create(AuctionSession session) {
        if (session.getStatus() == null) session.setStatus(1);
        sessionMapper.insert(session);
        return session;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuctionSession update(AuctionSession session) {
        if (session.getId() == null) throw new IllegalArgumentException("专场ID不能为空");
        if (sessionMapper.selectById(session.getId()) == null) throw new IllegalArgumentException("专场不存在");
        sessionMapper.updateById(session);
        return sessionMapper.selectById(session.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sessionMapper.deleteById(id);
    }
}
