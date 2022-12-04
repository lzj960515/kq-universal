package com.kqinfo.universal.delay.task.test;

import com.kqinfo.universal.delay.task.thread.ThreadUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class ThreadUtilTest {

    @Test
    public void testGetThreadCount(){
        System.out.println(ThreadUtil.getSuitableThreadCount());
    }
}
