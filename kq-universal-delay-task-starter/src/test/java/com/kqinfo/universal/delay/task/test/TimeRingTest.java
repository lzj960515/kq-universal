package com.kqinfo.universal.delay.task.test;

import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class TimeRingTest {

    @Test
    public void testGetSecond(){
        long time = System.currentTimeMillis();
        System.out.println((int) ((time / 1000) % 60));
    }
}
