package com.kqinfo.universal.cache.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kqinfo.universal.cache.core.KqCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
public class KqCacheManagerListTest {

    @Resource
    private KqCacheManager kqCacheManager;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    public void clear(){
        stringRedisTemplate.delete("test");
        stringRedisTemplate.delete("test_second");
        stringRedisTemplate.delete("test_lock");
    }

    @Test
    public void test() throws IOException {
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

    @Test
    public void testAsyncSecond() throws IOException {

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


    private void testCache(){
        try {
            List<User> data = kqCacheManager.getListCache("test", User.class, this::getData);
            log.info("获取到数据: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testSecondCache(){
        try {
            List<User> data = kqCacheManager.getListCacheHasSecond("test", User.class, this::getData);
            log.info("获取到数据: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testAsyncSecondCache(){
        try {
            List<User> data = kqCacheManager.asyncGetListCacheHasSecond("test", User.class, this::getData);
            log.info("获取到数据: " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<User> getData()  {
        try {
            // 模拟业务等待1s
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        return Collections.singletonList(user);
    }

    @Test
    public void testJson(){
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        List<User> users = Collections.singletonList(user);
        String cache = JSON.toJSONString(users);
        List<User> users1 = JSON.parseObject(cache, new TypeReference<List<User>>() {
        });
        System.out.println(users1);
    }

}
