package com.kqinfo.universal.redis.test;

import com.kqinfo.universal.redis.annotation.RedisLock;
import com.kqinfo.universal.redis.enums.LockStrategy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 2.7.0
 */
@Component
public class Job {


    @Scheduled(cron = "0/1 * * * * *")
    @RedisLock(name = "job", keys = {"'xx'"}, lockStrategy = LockStrategy.SKIP_AND_RETURN_NULL)
    public void job(){
        System.out.println(LocalDateTime.now());
    }
}
