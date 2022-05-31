package com.kqinfo.universal.retry.test;

import com.kqinfo.universal.retry.annotation.Retry;
import org.springframework.stereotype.Service;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class TestService {

    @Retry(retryFor = Exception.class)
    public void simple(String name, int age){
        System.out.println(name +":"+ age);
        throw new RuntimeException("xxx");
    }

    @Retry(retryFor = Exception.class, fixedRate = 1)
    public void multiParam(Order order, Long id, String a){
        throw new RuntimeException("测试失败");
    }
}
