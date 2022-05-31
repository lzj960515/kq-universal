package com.kqinfo.universal.async.config;

import org.springframework.aop.framework.AopContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class TestService {

    @Async
    public void async(){
        System.out.println("async start!");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("async end!");
    }

    public void sync(){
        async();
    }

    public void userAopContext(){
        TestService currentProxy = (TestService) AopContext.currentProxy();
        currentProxy.async();
    }

    @Async
    public void asyncEx(){
        System.out.println("async start!");
        throw new RuntimeException("xxx");
    }
}
