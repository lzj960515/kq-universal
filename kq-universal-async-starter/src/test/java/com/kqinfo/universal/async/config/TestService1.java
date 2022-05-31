package com.kqinfo.universal.async.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Zijian Liao
 * @since
 */
@Service
public class TestService1 {


    @Autowired
    private TestService2 testService2;

    @Async
    public void test(){
        System.out.println("xxx");
    }
}
