package com.kqinfo.universal.retry.test;

import com.kqinfo.universal.retry.handler.RetryHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RetryTest {

    @Resource
    private TestService testService;
    @Resource
    private RetryHandler retryHandler;

    @Test
    public void simpleTest(){
        testService.simple("aa", 1);
    }

    @Test
    public void multiParamTest(){
        Order order = new Order(1, LocalDateTime.now(), new Goods("apple", 12.11));
        testService.multiParam(order, 1L, "abcd");
    }

    @Test
    public void retry() {
        retryHandler.execute();
    }

}
