package com.kqinfo.universal.redis.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.BatchOptions;
import org.redisson.api.BatchResult;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RAtomicLongAsync;
import org.redisson.api.RBatch;
import org.redisson.api.RBitSet;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * redisson test
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    /*@Before
    public void before(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.65.206:6379")
                .setPassword("123456");
        redissonClient = Redisson.create(config);
    }*/
    @After
    public void after(){
        redissonClient.shutdown();
    }

    @Test
    public void quickTest() {
        RAtomicLong aLong = redissonClient.getAtomicLong("long");
        aLong.set(11L);
        RBucket<Object> bucket = redissonClient.getBucket("string");
        bucket.set("xxxx");
        System.out.println(bucket.get());

    }

    @Test
    public void batchTest(){
        BatchOptions options = BatchOptions.defaults()
                // 指定执行模式
                // ExecutionMode.REDIS_READ_ATOMIC - 所有命令缓存在Redis节点中，以原子性事务的方式执行。
                // ExecutionMode.REDIS_WRITE_ATOMIC - 所有命令缓存在Redis节点中，以原子性事务的方式执行。
                // ExecutionMode.IN_MEMORY - 所有命令缓存在Redisson本机内存中统一发送，但逐一执行（非事务）。默认模式。
                // ExecutionMode.IN_MEMORY_ATOMIC - 所有命令缓存在Redisson本机内存中统一发送，并以原子性事务的方式执行。
                .executionMode(BatchOptions.ExecutionMode.IN_MEMORY)
                // 告知Redis不用返回结果（可以减少网络用量）
                .skipResult()
                // 将写入操作同步到从节点
                // 同步到2个从节点，等待时间为1秒钟
                //.syncSlaves(2, 1, TimeUnit.SECONDS)
                // 处理结果超时为2秒钟
                .responseTimeout(2, TimeUnit.SECONDS)
                // 命令重试等待间隔时间为2秒钟
                .retryInterval(2, TimeUnit.SECONDS)
                // 命令重试次数。仅适用于未发送成功的命令
                .retryAttempts(4);
        RBatch batch = redissonClient.createBatch(options);
        batch.getMap("test").fastPutAsync("1", "2");
        batch.getMap("test").fastPutAsync("2", "3");
        batch.getMap("test").putAsync("2", "5");
//        batch.getAtomicLongAsync("counter").incrementAndGetAsync();
//        batch.getAtomicLongAsync("counter").incrementAndGetAsync();
        RAtomicLongAsync xx = batch.getAtomicLong("xx");
//        xx.expireAsync()
        BatchResult res = batch.execute();
        // 或者
        RFuture<BatchResult<?>> asyncRes = batch.executeAsync();
        List<?> response = res.getResponses();
        int syncedSlaves = res.getSyncedSlaves();
    }

    @Test
    public void lockTest() throws InterruptedException {
        String key = "1213";
        CountDownLatch countDownLatch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            new Thread(()->{
                System.out.println("线程："+ Thread.currentThread().getName() + "准备获取锁");
                RLock fairLock = redissonClient.getLock(key);
                fairLock.lock(2, TimeUnit.SECONDS);
                try {
                    System.out.println("线程：" + Thread.currentThread().getName() + "获取到锁: " + LocalDateTime.now().getSecond());
//                    Thread.sleep(5000);
                    long l = System.currentTimeMillis();
                    for (;;){
                        if(l + 5000 < System.currentTimeMillis()){
                            break;
                        }
                    }
                }finally {
                    if (fairLock.isHeldByCurrentThread()) {
                        fairLock.unlock();
                        System.out.println("线程：" + Thread.currentThread().getName() + "解锁");

                    }
//                    boolean b = fairLock.forceUnlock();
                    /*try {
                        if(fairLock.isHeldByCurrentThread()){
                            Boolean aBoolean = fairLock.forceUnlockAsync().get();
                        }
                        System.out.println(fairLock.isLocked());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }*/
                    countDownLatch.countDown();
//                    fairLock.forceUnlock();
                }
            },"thread-"+i).start();
        }
        countDownLatch.await();
    }

    @Test
    public void longTest(){
        RAtomicLong xx = redissonClient.getAtomicLong("xx");
//        xx.addAndGet()
//        redissonClient.
    }

    @Test
    public void bloomFilter(){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("xx");
        // 初始化布隆过滤器，预计统计元素数量为55000000，期望误差率为0.03
        bloomFilter.tryInit(55000000L, 0.03);
        System.out.println(bloomFilter.add("a"));
        System.out.println(bloomFilter.add("b"));
        System.out.println(bloomFilter.contains("a"));
        System.out.println(bloomFilter.count());
    }

    @Test
    public void testLeaseTime() throws InterruptedException {
        String key = "aa";
        new Thread(() -> {
            RLock lock = redissonClient.getLock(key);
            try {
                Assert.assertTrue(lock.tryLock(0, 10, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(5000);

        new Thread(() -> {
            RLock lock = redissonClient.getLock(key);
            try {
                Assert.assertFalse(lock.tryLock(0, 10, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(6000);

        new Thread(() -> {
            RLock lock = redissonClient.getLock(key);
            try {
                Assert.assertTrue(lock.tryLock(0, 10, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void testIsLocked(){
        String key = "aa";
        RLock lock = redissonClient.getLock(key);
        Assert.assertFalse(lock.isLocked());
        Assert.assertFalse(lock.isHeldByCurrentThread());
        lock.lock(5L, TimeUnit.SECONDS);
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isHeldByCurrentThread());
        lock.unlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertFalse(lock.isHeldByCurrentThread());
    }
}
