package com.kqinfo.universal.async.config;

import com.kqinfo.universal.async.manager.NameThreadFactory;
import com.kqinfo.universal.async.manager.ThreadPoolManager;
import com.kqinfo.universal.async.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Zijian Liao
 * @since 1.14.0
 */
public class ThreadPoolManagerTest {

    /**
     * 该方式将等待线程池中所有任务执行结束后结束进程
     */
    @Test
    public void testHok(){
        final ExecutorService executor = ThreadPoolManager.getExecutor("test");
        executor.execute(() -> {
            System.out.println("开始执行任务");
            ThreadUtil.sleep(5000);
            System.out.println("执行任务结束");
        });
        System.out.println("测试完成");
    }

    /**
     * 该方式不会等待线程池中任务完成，将直接结束进程
     */
    @Test
    public void testNoHok(){
        final int count = ThreadUtil.getSuitableThreadCount();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(count, count, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(200), new NameThreadFactory("test"));
        executor.execute(() -> {
            System.out.println("开始执行任务");
            ThreadUtil.sleep(3000);
            System.out.println("执行任务结束");
        });
        System.out.println("测试完成");
    }
}
