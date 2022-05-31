package com.kqinfo.universal.delay.task.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zijian Liao
 * @since 1.14.0
 */
public class NameThreadFactory implements ThreadFactory {
    
    private final AtomicInteger id = new AtomicInteger(0);
    
    private final String name;
    
    public NameThreadFactory(String name) {
        if(!name.endsWith("-")){
            name = name + "-";
        }
        this.name = name;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        String threadName = name + id.getAndIncrement();
        Thread thread = new Thread(r, threadName);
        thread.setDaemon(true);
        return thread;
    }
}