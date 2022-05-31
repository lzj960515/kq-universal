package com.kqinfo.universal.redis.enums;

import com.kqinfo.universal.redis.annotation.RedisLock;
import com.kqinfo.universal.redis.exception.RedisLockAcquireTimeoutException;
import com.kqinfo.universal.redis.exception.RedisLockFailFastException;
import com.kqinfo.universal.redis.handler.LockStrategyHandler;
import com.kqinfo.universal.redis.util.LockContext;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 加锁策略
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
public enum LockStrategy implements LockStrategyHandler {

    /**
     * 尝试获取锁，未获取到锁抛出异常
     *
     * @see RedisLockFailFastException
     */
    FAIL_FAST() {
        @Override
        public void lock(RLock lock, RedisLock redisLock) throws InterruptedException {
            try {
                boolean locked = lock.tryLock(0, redisLock.leaseTime(), TimeUnit.SECONDS);
                if (!locked) {
                    throw new RedisLockFailFastException(StringUtils.hasText(redisLock.exceptionMessage()) ? redisLock.exceptionMessage() : FAST_FAIL_MESSAGE);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
    },
    /**
     * 等待获取锁，直到获取锁成功
     */
    KEEP_ACQUIRE() {
        @Override
        public void lock(RLock lock, RedisLock redisLock) {
            lock.lock(redisLock.leaseTime(), TimeUnit.SECONDS);
        }
    },
    /**
     * 等待获取锁，超过一定时间内仍未获取到锁抛出异常
     *
     * @see RedisLockAcquireTimeoutException
     */
    KEEP_ACQUIRE_TIMEOUT() {
        @Override
        public void lock(RLock lock, RedisLock redisLock) throws InterruptedException {
            try {
                boolean locked = lock.tryLock(redisLock.waitTime(), redisLock.leaseTime(), TimeUnit.SECONDS);
                if (!locked) {
                    throw new RedisLockAcquireTimeoutException(StringUtils.hasText(redisLock.exceptionMessage()) ? redisLock.exceptionMessage() : KEEP_ACQUIRE_TIMEOUT_MESSAGE);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
    },

    SKIP_AND_RETURN_NULL(){
        @Override
        public void lock(RLock lock, RedisLock redisLock) throws InterruptedException {
            try {
                boolean locked = lock.tryLock(0, redisLock.leaseTime(), TimeUnit.SECONDS);
                if (locked) {
                    LockContext.setSuccess();
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
    };


}
