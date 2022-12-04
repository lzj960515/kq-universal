package com.kqinfo.universal.delay.task.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理器，维护从异步组件中获取的所有线程，并在系统停止时对所有线程进行shutdown
 * @author Zijian Liao
 * @since 1.14.0
 */
public final class ThreadPoolManager {

    public static ExecutorService getExecutor(String threadName) {
        final int count = ThreadUtil.getSuitableThreadCount();
        return new ThreadPoolExecutor(count, count*2, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(Integer.MAX_VALUE), new NameThreadFactory(threadName));
    }

    public static ExecutorService getSingleExecutor(String threadName) {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(Integer.MAX_VALUE), new NameThreadFactory(threadName));
    }

    public static ScheduledThreadPoolExecutor getSingleScheduledExecutor(String threadName) {
        return new ScheduledThreadPoolExecutor(1,  new NameThreadFactory(threadName));
    }
}
