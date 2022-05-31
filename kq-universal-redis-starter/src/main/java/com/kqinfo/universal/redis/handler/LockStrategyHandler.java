package com.kqinfo.universal.redis.handler;

import com.kqinfo.universal.redis.annotation.RedisLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 获取锁策略处理器器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface LockStrategyHandler {
    String FAST_FAIL_MESSAGE = "获取锁失败";
    String KEEP_ACQUIRE_TIMEOUT_MESSAGE = "获取锁超时";

    /**
     * 获取锁
     * @param lock {@link RLock}
     * @param redisLock {@link RedisLock}
     * @throws InterruptedException 中断异常
     */
    void lock(RLock lock, RedisLock redisLock) throws InterruptedException;
}
