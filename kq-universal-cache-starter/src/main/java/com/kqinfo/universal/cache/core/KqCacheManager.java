package com.kqinfo.universal.cache.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存管理
 *
 * @author Zijian Liao
 * @since cache
 */
public class KqCacheManager {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private KqCacheAsyncHandler kqCacheAsyncHandler;

    public <T> T getCache(String key, Class<T> type, Supplier<T> supplier) throws Exception {
        return this.getCache(key, 30L, 180L, type, supplier);
    }

    public <T> T getCache(String key, long lockTime, long cacheTime, Class<T> type, Supplier<T> supplier) throws Exception {
        Assert.notNull(key, "缓存key不能为空");
        // 获取缓存
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(cache)) {
            return JSON.parseObject(cache, type);
        }
        RLock lock = redissonClient.getLock(getLockKey(key));
        boolean locked = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        if (locked) {
            try {
                // 再次获取缓存
                cache = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.hasText(cache)) {
                    return JSON.parseObject(cache, type);
                }
                T value = supplier.get();
                stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), cacheTime, TimeUnit.SECONDS);
                return value;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return null;
    }

    public <T> T getCacheHasSecond(String key, Class<T> type, Supplier<T> supplier) throws Exception {
        return this.getCacheHasSecond(key, 30L, 180L, 60 * 60 * 24L, type, supplier);
    }

    public <T> T getCacheHasSecond(String key, long lockTime, long cacheTime, long secondCacheTime, Class<T> type, Supplier<T> supplier) throws Exception {
        Assert.notNull(key, "缓存key不能为空");
        // 获取缓存
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(cache)) {
            return JSON.parseObject(cache, type);
        }
        // 一级缓存不存在时加锁 进行缓存刷新
        RLock lock = redissonClient.getLock(getLockKey(key));
        boolean locked = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        if (locked) {
            try {
                // 再次获取缓存
                cache = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.hasText(cache)) {
                    return JSON.parseObject(cache, type);
                }
                T value = supplier.get();
                String jsonValue = JSON.toJSONString(value);
                // 保存一级缓存
                stringRedisTemplate.opsForValue().set(key, jsonValue, cacheTime, TimeUnit.SECONDS);
                // 保存二级缓存
                stringRedisTemplate.opsForValue().set(getSecondKey(key), jsonValue, secondCacheTime, TimeUnit.SECONDS);
                return value;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        // 加锁成功的线程不会走到这里，走到这里必然是加锁失败时的线程
        // 尝试从二级缓存获取数据
        String secondCache = stringRedisTemplate.opsForValue().get(getSecondKey(key));
        if(StringUtils.hasText(secondCache)){
            return JSON.parseObject(secondCache, type);
        }
        return null;
    }

    public <T> T asyncGetCacheHasSecond(String key, Class<T> type, Supplier<T> supplier) throws Exception {
        return this.asyncGetCacheHasSecond(key, 30L, 180L, 60 * 60 * 24L, type, supplier);
    }

    public <T> T asyncGetCacheHasSecond(String key, long lockTime, long cacheTime, long secondCacheTime, Class<T> type, Supplier<T> supplier) throws Exception {
        Assert.notNull(key, "缓存key不能为空");
        // 获取缓存
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(cache)) {
            return JSON.parseObject(cache, type);
        }
        // 进行缓存刷新
        kqCacheAsyncHandler.getDataAndSetCache(key, lockTime, cacheTime, secondCacheTime, type, supplier);
        // 尝试从二级缓存获取数据
        String secondCache = stringRedisTemplate.opsForValue().get(getSecondKey(key));
        if(StringUtils.hasText(secondCache)){
            return JSON.parseObject(secondCache, type);
        }
        return null;
    }


    //----------List--------

    public <T> List<T> getListCache(String key, Class<T> type, Supplier<List<T>> supplier) throws Exception {
        return this.getListCache(key, 30L, 180L, type, supplier);
    }

    public <T> List<T> getListCache(String key, long lockTime, long cacheTime, Class<T> type, Supplier<List<T>> supplier) throws Exception {
        Assert.notNull(key, "缓存key不能为空");
        // 获取缓存
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(cache)) {
            return JSON.parseArray(cache, type);
        }
        RLock lock = redissonClient.getLock(getLockKey(key));
        boolean locked = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        if (locked) {
            try {
                // 再次获取缓存
                cache = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.hasText(cache)) {
                    return JSON.parseArray(cache, type);
                }
                List<T> value = supplier.get();
                stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), cacheTime, TimeUnit.SECONDS);
                return value;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return null;
    }

    public <T> List<T> getListCacheHasSecond(String key, Class<T> type, Supplier<List<T>> supplier) throws Exception {
        return this.getListCacheHasSecond(key, 30L, 180L, 60 * 60 * 24L, type, supplier);
    }

    public <T> List<T> getListCacheHasSecond(String key, long lockTime, long cacheTime, long secondCacheTime, Class<T> type, Supplier<List<T>> supplier) throws Exception {
        Assert.notNull(key, "缓存key不能为空");
        // 获取缓存
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(cache)) {
            return JSON.parseArray(cache, type);
        }
        // 一级缓存不存在时加锁 进行缓存刷新
        RLock lock = redissonClient.getLock(getLockKey(key));
        boolean locked = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        if (locked) {
            try {
                // 再次获取缓存
                cache = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.hasText(cache)) {
                    return JSON.parseArray(cache, type);
                }
                List<T> value = supplier.get();
                String jsonValue = JSON.toJSONString(value);
                // 保存一级缓存
                stringRedisTemplate.opsForValue().set(key, jsonValue, cacheTime, TimeUnit.SECONDS);
                // 保存二级缓存
                stringRedisTemplate.opsForValue().set(getSecondKey(key), jsonValue, secondCacheTime, TimeUnit.SECONDS);
                return value;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        // 加锁成功的线程不会走到这里，走到这里必然是加锁失败时的线程
        // 尝试从二级缓存获取数据
        String secondCache = stringRedisTemplate.opsForValue().get(getSecondKey(key));
        if(StringUtils.hasText(secondCache)){
            return JSON.parseObject(secondCache, new TypeReference<List<T>>(){});
        }
        return null;
    }

    public <T> List<T> asyncGetListCacheHasSecond(String key, Class<T> type, Supplier<List<T>> supplier) throws Exception {
        return this.asyncGetListCacheHasSecond(key, 30L, 180L, 60 * 60 * 24L, type, supplier);
    }

    public <T> List<T> asyncGetListCacheHasSecond(String key, long lockTime, long cacheTime, long secondCacheTime, Class<T> type, Supplier<List<T>> supplier) throws Exception {
        Assert.notNull(key, "缓存key不能为空");
        // 获取缓存
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(cache)) {
            return JSON.parseArray(cache, type);
        }
        // 进行缓存刷新
        kqCacheAsyncHandler.getListDataAndSetCache(key, lockTime, cacheTime, secondCacheTime, supplier);
        // 尝试从二级缓存获取数据
        String secondCache = stringRedisTemplate.opsForValue().get(getSecondKey(key));
        if(StringUtils.hasText(secondCache)){
            return JSON.parseArray(secondCache, type);
        }
        return null;
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
