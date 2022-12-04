package com.kqinfo.universal.stream.core;

import com.kqinfo.universal.stream.annotation.StreamListener;
import com.kqinfo.universal.stream.util.Group;
import com.kqinfo.universal.stream.util.MessageHandler;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.10.0
 */
public class RedisStreamListenerContext extends StreamListenerContext {

    private final RedisConnectionFactory redisConnectionFactory;

    private final List<StreamMessageListenerContainer<String, ?>> containerList = new ArrayList<>();

    public RedisStreamListenerContext(RedisConnectionFactory redisConnectionFactory, MessageHandler messageHandler) {
        super(messageHandler);
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    <T> void doRegisterConsumer(StreamListener streamListener, Object bean, Method method, Class<T> type) {
        String queue = super.findQueue(streamListener.queue());
        String group = streamListener.group();
        String consumer = streamListener.consumer();

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, T>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(streamListener.batchSize())
                        .targetType(type)
                        .pollTimeout(Duration.ZERO)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, T>> container = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        createGroup(queue, group);

        consumePendingMessage(queue, group, consumer, type);

        container.receive(Consumer.from(group, consumer),
                StreamOffset.create(queue, ReadOffset.lastConsumed()),
                new StreamMessageListener<T>(group, consumer));

        containerList.add(container);
        container.start();
    }


    private void createGroup(String queue, String group) {
        // 判断是否存在group
        try{
            List<Group> groups = messageHandler.groups(queue);
            if (groups.stream().noneMatch(item -> group.equals(item.getGroupName()))) {
                Assert.isTrue(messageHandler.subscribe(queue, group, null), "Cannot create group with name '" + group + "'");
            }
        }catch (InvalidDataAccessApiUsageException | RedisSystemException e){
            // 异常说明key不存在 往队列中添加一条初始化队列消息
            String id = messageHandler.convertAndSend(queue, "init queue");
            Assert.hasText(id, "Cannot initialize stream with key '" + queue + "'");
            // 再次创建
            Assert.isTrue(messageHandler.subscribe(queue, group, null), "Cannot create group with name '" + group + "'");
        }

    }

    private <T> void consumePendingMessage(String queue, String group, String consumer, Class<T> targetType){
        List<MessageRecord<T>> messageRecordList = messageHandler.pending(queue, group, consumer, targetType);
        messageRecordList.forEach(messageRecord -> {
            routeMessage(queue, group, consumer, messageRecord.getMessage(), messageRecord.getRecordId());
        });
    }



    @Override
    void doDestroy() {
        this.containerList.forEach(StreamMessageListenerContainer::stop);
    }

    public class StreamMessageListener<T> implements org.springframework.data.redis.stream.StreamListener<String, ObjectRecord<String, T>> {

        private final String group;
        private final String consumer;

        public StreamMessageListener(String group, String consumer) {
            this.group = group;
            this.consumer = consumer;
        }

        @Override
        public void onMessage(ObjectRecord<String, T> message) {
            routeMessage(message.getStream(), group, consumer, message.getValue(), message.getId().getValue());
        }
    }
}
