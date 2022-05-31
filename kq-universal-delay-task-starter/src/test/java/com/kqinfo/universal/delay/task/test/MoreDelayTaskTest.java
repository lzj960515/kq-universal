package com.kqinfo.universal.delay.task.test;

import com.kqinfo.universal.delay.task.core.DelayTaskTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 并发量测试
 * @author Zijian Liao
 * @since 1.0.9
 */
@SpringBootTest
public class MoreDelayTaskTest {

    @Resource
    private DelayTaskTemplate delayTaskTemplate;

    /**
     * 每秒1000个延迟任务
     */
    @Test
    public void testMoreDelayTest() throws IOException {
        // 开10个线程
        for(int n = 0; n < 10; n++ ){
            Thread t  = new Thread(() -> {
                // 测试100s
                for (int i = 0; i < 100; i++){
                    // 每个线程存100个
                    for(int j = 0; j < 100; j ++ ){
                        delayTaskTemplate.save("demoJob" + i%2, i + "", LocalDateTime.now().plusSeconds(10+i), "测试任务");
                    }
                }
            });
            t.start();
        }
        System.out.println("任务保存完毕");
        System.in.read();
    }

}
