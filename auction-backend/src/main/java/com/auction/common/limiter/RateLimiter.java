package com.auction.common.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiter {

    private final StringRedisTemplate redisTemplate;

    /**
     * 3秒内同一用户对同一拍品最多5次出价。
     */
    public boolean checkBidRate(Long userId, Long itemId) {
        String key = "rate:bid:" + userId + ":" + itemId;
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L) {
                redisTemplate.expire(key, 3, TimeUnit.SECONDS);
            }
            return count == null || count <= 5;
        } catch (Exception ex) {
            // 本地无Redis时不阻断主流程，避免开发环境不可用。
            log.warn("Redis rate limiter unavailable, fallback allow. key={}", key);
            return true;
        }
    }
}
