package com.kqinfo.universal.redis.test;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronTrigger;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class CronTriggerTest {

    @Test
    public void testCronTrigger(){
        CronTrigger cronTrigger = new CronTrigger("60/5 * * * * ?");
    }
}
