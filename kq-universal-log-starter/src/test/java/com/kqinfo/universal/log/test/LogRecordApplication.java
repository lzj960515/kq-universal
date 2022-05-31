package com.kqinfo.universal.log.test;

import com.kqinfo.universal.log.service.LogRecordService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
@RestController
@SpringBootApplication
public class LogRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogRecordApplication.class, args);
    }

    @Resource
    private TestService testService;

    @GetMapping("/test")
    public String test(){
        Order order = new Order();
        order.setId("1234");
        testService.test(order);
        return "ok";
    }
}
