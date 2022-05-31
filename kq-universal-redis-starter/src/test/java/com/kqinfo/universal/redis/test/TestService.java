package com.kqinfo.universal.redis.test;

import com.kqinfo.universal.redis.annotation.RedisLock;
import com.kqinfo.universal.redis.enums.LockStrategy;
import com.kqinfo.universal.redis.enums.LockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 测试service
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
@Service
public class TestService {

    @Resource
    private TestServiceB testServiceB;

    @RedisLock(name = "hello", keys = "#hello")
    public String failfast(String hello){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RedisLock(name = "hello", keys = "#hello", lockStrategy = LockStrategy.KEEP_ACQUIRE)
    public String keepAcquire(String hello){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RedisLock(name = "hello", keys = "#hello", lockStrategy = LockStrategy.KEEP_ACQUIRE_TIMEOUT, waitTime = 10)
    public String keepAcquireTimeout(String hello){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RedisLock(name = "spel", keys = {"#user.name", "#user.age"})
    public String spel(User user){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RedisLock(name = "readwrite", keys = {"#hello"}, lockType = LockType.ReadLock, lockStrategy = LockStrategy.KEEP_ACQUIRE)
    public String readLock(String hello){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RedisLock(name = "readwrite", keys = {"#hello"}, lockType = LockType.WriteLock, lockStrategy = LockStrategy.KEEP_ACQUIRE)
    public String writeLock(String hello){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RedisLock(name = "autorenew", keys = {"#hello"}, lockStrategy = LockStrategy.FAIL_FAST, leaseTime = -1)
    public String autoRenew(String hello){
        try {
            System.out.println(Thread.currentThread().getName() +  "获取到锁");
            Thread.sleep(60000);
            System.out.println("方法执行完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RedisLock(name = "reentrant", keys = "#hello")
    public String reentrant(String hello){
        System.out.println("进入A service");
        testServiceB.reentrant(hello);
        return "ok";
    }

    @RedisLock(name = "message", keys = "#hello", exceptionMessage = "自定义异常~")
    public String message(String hello){
        return "ok";
    }

}
