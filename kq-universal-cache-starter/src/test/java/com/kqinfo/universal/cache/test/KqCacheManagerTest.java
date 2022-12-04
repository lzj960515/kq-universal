package com.kqinfo.universal.cache.test;

import com.alibaba.fastjson.JSONObject;
import com.kqinfo.universal.cache.core.KqCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Zijian Liao
 * @since cache
 */
@Slf4j
@SpringBootTest
public class KqCacheManagerTest {

    @Resource
    private KqCacheManager kqCacheManager;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() throws IOException {
        stringRedisTemplate.delete("test");
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0, 10).forEach(i -> {
            executorService.submit(this::testCache);
        });
        try {
            // 等待2s再次尝试
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IntStream.range(0, 10).forEach(i -> {
            executorService.submit(this::testCache);
        });
        System.in.read();
    }

    @Test
    public void testSecond() throws IOException {
        stringRedisTemplate.delete("test");
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0, 10).forEach(i -> {
            executorService.submit(this::testSecondCache);
        });
        try {
            // 等待2s再次尝试
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IntStream.range(0, 10).forEach(i -> {
            executorService.submit(this::testSecondCache);
        });
        System.in.read();
    }

    private void testSecondCache(){
        try {
            JSONObject data = kqCacheManager.getCacheHasSecond("test", JSONObject.class, this::getData);
            log.info("获取到数据: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAsyncSecond() throws IOException {
        stringRedisTemplate.delete("test");
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0, 10).forEach(i -> {
            executorService.submit(this::testAsyncSecondCache);
        });
        try {
            // 等待2s再次尝试
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IntStream.range(0, 10).forEach(i -> {
            executorService.submit(this::testAsyncSecondCache);
        });
        System.in.read();
    }

    private void testAsyncSecondCache(){
        try {
            JSONObject data = kqCacheManager.asyncGetCacheHasSecond("test", JSONObject.class, this::getData);
            log.info("获取到数据: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testCache(){
        try {
            JSONObject data = kqCacheManager.getCache("test", JSONObject.class, this::getData);
            log.info("获取到数据: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getData()  {
        try {
            // 模拟业务等待1s
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject data = new JSONObject();
        data.put("name", "张三");
        return data;
    }

}
