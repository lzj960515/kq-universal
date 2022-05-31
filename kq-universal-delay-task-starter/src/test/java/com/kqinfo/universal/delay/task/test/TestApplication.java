package com.kqinfo.universal.delay.task.test;

import com.kqinfo.universal.delay.task.annotation.EnableDelayTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@EnableDelayTask
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}