package com.kqinfo.universal.redis.exception;

/**
 * 加锁超时异常
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class RedisLockAcquireTimeoutException extends RedisLockException {

    public RedisLockAcquireTimeoutException(String message){
        super(message);
    }

}
