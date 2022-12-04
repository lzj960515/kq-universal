package com.kqinfo.universal.stream.test;

import com.kqinfo.universal.stream.annotation.StreamListener;
import com.kqinfo.universal.stream.core.StreamChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Zijian Liao
 * @since 1.4.0
 */
@Slf4j
@Service
public class TestService {

    @StreamListener(queue = "${queue-name}")
    public void onUserMessage(StreamChannel<User> streamChannel){
        User user = streamChannel.getMessage();
        log.info("消费queue:{}中的信息:{}, 消息id:{}", streamChannel.getQueue(),  streamChannel.getMessage(),  streamChannel.getRecordId());
        streamChannel.acknowledge();
    }

    @StreamListener(queue = "order", group = "pay")
    public void OnOrderMessage1(StreamChannel<Order> streamChannel){
        // 测试同组项目
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Order order = streamChannel.getMessage();
        log.info("消费queue-order:{}中的信息:{}, 消息id:{}", streamChannel.getQueue(),  streamChannel.getMessage(),  streamChannel.getRecordId());
        streamChannel.acknowledge();
    }

    @StreamListener(queue = "order", group = "store")
    public void OnOrderMessage2(StreamChannel<Order> streamChannel){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Order order = streamChannel.getMessage();
        log.info("消费queue-store:{}中的信息:{}, 消息id:{}", streamChannel.getQueue(),  streamChannel.getMessage(),  streamChannel.getRecordId());
        streamChannel.acknowledge();
    }

    /**
     * 测试消息 不ack，再起启动服务是否会再接到消息
     * 结论： 会
     */
    @StreamListener(queue = "order", group = "replay")
    public void OnOrderMessage3(StreamChannel<Order> streamChannel){
        Order order = streamChannel.getMessage();
        log.info("消费queue-replay:{}中的信息:{}, 消息id:{}", streamChannel.getQueue(),  streamChannel.getMessage(),  streamChannel.getRecordId());
        streamChannel.acknowledge();
    }
}
