package com.auction.modules.log.service.impl;

import com.auction.modules.log.entity.OpLog;
import com.auction.modules.log.mapper.OpLogMapper;
import com.auction.modules.log.service.OpLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpLogServiceImpl implements OpLogService {

    private final OpLogMapper opLogMapper;

    @Override
    public void log(Long userId, String action, String targetType, Long targetId, String detail, boolean success) {
        try {
            OpLog logEntity = new OpLog();
            logEntity.setUserId(userId);
            logEntity.setAction(action);
            logEntity.setTargetType(targetType);
            logEntity.setTargetId(targetId);
            logEntity.setDetail(detail);
            logEntity.setSuccess(success ? 1 : 0);
            opLogMapper.insert(logEntity);
        } catch (Exception ex) {
            log.warn("Failed to write operation log: {}", ex.getMessage());
        }
    }

    @Override
    public List<OpLog> listRecent(Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 50 : Math.min(limit, 500);
        return opLogMapper.selectList(
                new LambdaQueryWrapper<OpLog>().orderByDesc(OpLog::getId).last("LIMIT " + safeLimit)
        );
    }
}
