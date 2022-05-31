package com.kqinfo.universal.redis.test;

import com.kqinfo.universal.redis.annotation.RedisLock;
import org.springframework.stereotype.Service;

/**
 * 用来测试可重入锁
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class TestServiceB {

    @RedisLock(name = "reentrant", keys = "#hello")
    public String reentrant(String hello){
        System.out.println("进入B service");
        return "ok";
    }
}
