package com.kqinfo.universal.redis.test;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * redis lock test
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
public class RedisLockTest {

    @Resource
    private TestService testService;

    /**
     *  测试快速失败
     */
    @Test
    public void failfastTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0,10).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.failfast("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getClass());
            }
        }));
        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }

    /**
     *  测试等待加锁
     */
    @Test
    public void keepAcquireTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0,10).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.keepAcquire("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getClass());
            }
        }));
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }


    /**
     *  测试等待加锁超时
     */
    @Test
    public void keepAcquireTimeoutTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0,10).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.keepAcquireTimeout("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getClass());
            }
        }));
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }

    @Test
    public void spelTest(){
        User user = new User();
        user.setName("zhangsan");
        user.setAge(12);
        testService.spel(user);
    }

    @Test
    public void readLockTest() throws InterruptedException {
        AtomicInteger success = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0,10).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.readLock("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
                success.incrementAndGet();
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getClass());
            }
        }));
        executorService.awaitTermination(60, TimeUnit.SECONDS);
        MatcherAssert.assertThat(10, CoreMatchers.is(success.get()));
    }

    /**
     * 读写锁测试
     */
    @Test
    public void readwriteLockTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0,10).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.readLock("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getClass());
            }
        }));

        ExecutorService executorService2 = Executors.newFixedThreadPool(6);
        IntStream.range(0,10).forEach(i-> executorService2.submit(() -> {
            try {
                String result = testService.writeLock("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getClass());
            }
        }));
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }

    /**
     * 测试锁自动续期
     */
    @Test
    public void autoRenewTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        IntStream.range(0,2).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.autoRenew("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getClass());
            }
        }));
        executorService.awaitTermination(120, TimeUnit.SECONDS);
    }

    @Test
    public void reentrantTest(){
        testService.reentrant("xx");
    }

    @Test
    public void messageTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        IntStream.range(0,2).forEach(i-> executorService.submit(() -> {
            try {
                String result = testService.message("xx");
                System.out.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + LocalDateTime.now().getSecond());
            } catch (Exception e) {
                System.err.println("线程:[" + Thread.currentThread().getName() + "]获取锁失败 " + e.getMessage());
            }
        }));
        executorService.awaitTermination(120, TimeUnit.SECONDS);
    }
}
