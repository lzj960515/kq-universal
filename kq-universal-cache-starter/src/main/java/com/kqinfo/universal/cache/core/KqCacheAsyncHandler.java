package com.kqinfo.universal.cache.core;

import com.alibaba.fastjson.JSON;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Zijian Liao
 * @since cache
 */
public class KqCacheAsyncHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;

    @Async
    public  <T> void getDataAndSetCache(String key, long lockTime, long cacheTime, long secondCacheTime, Class<T> type, Supplier<T> supplier) throws InterruptedException {
        // 加锁 进行缓存刷新
        RLock lock = redissonClient.getLock(getLockKey(key));
        boolean locked = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        if (locked) {
            try {
                // 再次获取缓存
                String cache = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.hasText(cache)) {
                    return ;
                }
                T value = supplier.get();
                String jsonValue = JSON.toJSONString(value);
                // 保存一级缓存
                stringRedisTemplate.opsForValue().set(key, jsonValue, cacheTime, TimeUnit.SECONDS);
                // 保存二级缓存
                stringRedisTemplate.opsForValue().set(getSecondKey(key), jsonValue, secondCacheTime, TimeUnit.SECONDS);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @Async
    public  <T> void getListDataAndSetCache(String key, long lockTime, long cacheTime, long secondCacheTime, Supplier<List<T>> supplier) throws InterruptedException {
        // 加锁 进行缓存刷新
        RLock lock = redissonClient.getLock(getLockKey(key));
        boolean locked = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        if (locked) {
            try {
                // 再次获取缓存
                String cache = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.hasText(cache)) {
                    return ;
                }
                List<T> value = supplier.get();
                String jsonValue = JSON.toJSONString(value);
                // 保存一级缓存
                stringRedisTemplate.opsForValue().set(key, jsonValue, cacheTime, TimeUnit.SECONDS);
                // 保存二级缓存
                stringRedisTemplate.opsForValue().set(getSecondKey(key), jsonValue, secondCacheTime, TimeUnit.SECONDS);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 获取加锁key
     */
    private String getLockKey(String key) {
        return key + "_lock";
    }

    /**
     * 获取二级缓存key
     */
    private String getSecondKey(String key) {
        return key + "_second";
    }
}
