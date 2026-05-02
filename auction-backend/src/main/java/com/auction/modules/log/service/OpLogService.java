package com.auction.modules.log.service;

import com.auction.modules.log.entity.OpLog;

import java.util.List;

public interface OpLogService {
    void log(Long userId, String action, String targetType, Long targetId, String detail, boolean success);
    List<OpLog> listRecent(Integer limit);
}
