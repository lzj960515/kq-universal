package com.kqinfo.universal.delay.task.thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Zijian Liao
 * @since 1.14.0
 */
public final class ThreadUtil {

    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);
    private static final int THREAD_MULTIPLE = 4;

    
    public static void countDown(CountDownLatch latch) {
        Objects.requireNonNull(latch, "latch");
        latch.countDown();
    }
    
    /**
     * Through the number of cores, calculate the appropriate number of threads; 1.5-2 times the number of CPU cores.
     *
     * @return thread count
     */
    public static int getSuitableThreadCount() {
        return getSuitableThreadCount(THREAD_MULTIPLE);
    }
    
    /**
     * Through the number of cores, calculate the appropriate number of threads.
     *
     * @param threadMultiple multiple time of cores
     * @return thread count
     */
    public static int getSuitableThreadCount(int threadMultiple) {
        final int coreCount = Runtime.getRuntime().availableProcessors();
        int workerCount = 1;
        while (workerCount < coreCount * threadMultiple) {
            workerCount <<= 1;
        }
        return workerCount;
    }
    

    /**
     * Shutdown thread pool.
     *
     * @param executor thread pool
     */
    public static void shutdownThreadPool(ExecutorService executor) {
        executor.shutdown();
        int retry = 3;
        while (retry > 0) {
            retry--;
            try {
                if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    return;
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            } catch (Throwable ex) {
                log.error("ThreadPoolManager shutdown executor has error", ex);
            }
        }
        executor.shutdownNow();
    }
    
    public static void addShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

}
