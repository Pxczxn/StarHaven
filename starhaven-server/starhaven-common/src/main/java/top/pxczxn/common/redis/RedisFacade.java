package top.pxczxn.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 可选增强：故障时降级，不阻断核心业务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisFacade {

    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception ex) {
            log.warn("Redis SET 降级 key={}", key, ex);
        }
    }

    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            log.warn("Redis GET 降级 key={}", key, ex);
            return null;
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ex) {
            log.warn("Redis DEL 降级 key={}", key, ex);
        }
    }

    public boolean tryLock(String key, Duration ttl) {
        try {
            Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl);
            return Boolean.TRUE.equals(ok);
        } catch (Exception ex) {
            log.warn("Redis LOCK 降级 key={}，允许继续业务", key, ex);
            return true;
        }
    }

    public void unlock(String key) {
        delete(key);
    }

    public void incrementZSet(String key, String member, double score) {
        try {
            redisTemplate.opsForZSet().incrementScore(key, member, score);
        } catch (Exception ex) {
            log.warn("Redis ZINCR 降级 key={}", key, ex);
        }
    }

    public Set<String> topZSet(String key, long count) {
        try {
            Set<String> values = redisTemplate.opsForZSet().reverseRange(key, 0, count - 1);
            return values == null ? Collections.emptySet() : values;
        } catch (Exception ex) {
            log.warn("Redis ZRANGE 降级 key={}", key, ex);
            return Collections.emptySet();
        }
    }

    public void expire(String key, long seconds) {
        try {
            redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.warn("Redis EXPIRE 降级 key={}", key, ex);
        }
    }
}
