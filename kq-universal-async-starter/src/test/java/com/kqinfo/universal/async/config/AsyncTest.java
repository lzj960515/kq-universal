package com.kqinfo.universal.async.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AsyncApplication.class)
public class AsyncTest {

    @Resource
    private TestService testService;

    @Test
    public void testAsync(){
        testService.async();
        System.out.println("test is end!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试同类中方法调用是否异步, 结果：否
     */
    @Test
    public void testSync(){
        testService.sync();
        System.out.println("test is end!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 报错，无法获取到AopCurrent
     */
    @Test
    public void testAopContext(){
        testService.userAopContext();
        System.out.println("test is end!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEx(){
        testService.asyncEx();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test is end!");
    }
}
