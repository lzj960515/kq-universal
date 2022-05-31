package com.kqinfo.universal.redis.lock;

import com.kqinfo.universal.redis.enums.LockType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;

/**
 * 工厂类，根据类型创建锁
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public final class LockFactory {

    public static RLock createLock(RedissonClient redissonClient, LockType lockType, String key){
        switch (lockType){
            case ReadLock:
                return redissonClient.getReadWriteLock(key).readLock();
            case WriteLock:
                return redissonClient.getReadWriteLock(key).writeLock();
            default:
                return redissonClient.getLock(key);
        }
    }
}
