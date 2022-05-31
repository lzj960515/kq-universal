package com.kqinfo.universal.redis.exception;

/**
 * 加锁失败异常
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class RedisLockFailFastException extends RedisLockException {

    public RedisLockFailFastException(String message){
        super(message);
    }
}
