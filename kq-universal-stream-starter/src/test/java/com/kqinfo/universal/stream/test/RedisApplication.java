package com.kqinfo.universal.stream.test;

import com.kqinfo.universal.stream.util.MessageHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@SpringBootApplication
@RestController
public class RedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @Resource
    private MessageHandler messageHandler;

    @GetMapping("/send")
    public String sendTest(){
        User user = new User();
        user.setName("王五");
        user.setAge(12);
        return messageHandler.convertAndSend("user", user);
    }

    @GetMapping("/sendOrder")
    public String sendOrderTest(){
        Order order = new Order();
        order.setId(12L);
        order.setName("order");
        order.setPrice(1.23);
        return messageHandler.convertAndSend("order", order);
    }
}
