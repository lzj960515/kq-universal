package com.kqinfo.universal.redis.exception;

/**
 * redis lock 异常
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class RedisLockException extends RuntimeException {

    RedisLockException(String message){
        super(message);
    }
}
