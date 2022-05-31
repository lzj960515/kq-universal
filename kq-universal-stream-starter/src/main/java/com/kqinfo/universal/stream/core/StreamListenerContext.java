package com.kqinfo.universal.stream.core;

import com.kqinfo.universal.stream.annotation.StreamListener;
import com.kqinfo.universal.stream.util.Group;
import com.kqinfo.universal.stream.util.MessageHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class StreamListenerContext implements ApplicationListener<ApplicationStartedEvent>, DisposableBean {

    protected final MessageHandler messageHandler;
    /**
     * key:group:consumer
     */
    protected final Map<String, MethodHandler> methodHandlerRepository = new ConcurrentHashMap<>(32);

    public StreamListenerContext(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        StreamListenerScanner.scan(event.getApplicationContext(), this);
    }


    public <T> void registerConsumer(StreamListener streamListener, Object bean, Method method, Class<T> type) {
        registerMethodHandler(streamListener, bean, method);
        doRegisterConsumer(streamListener, bean, method, type);
    }

    /**
     * 注册消费者
     * @param streamListener streamListener
     * @param bean bean
     * @param method  streamListener
     * @param type class type
     * @param <T> class type
     */
    abstract <T> void doRegisterConsumer(StreamListener streamListener, Object bean, Method method, Class<T> type);

    private void registerMethodHandler(StreamListener streamListener, Object bean, Method method) {
        String key = getKey(streamListener.queue(), streamListener.group(), streamListener.consumer());
        if (methodHandlerRepository.containsKey(key)) {
            throw new RuntimeException("队列{" + key + "}重复于 " + bean.getClass() + "." + method.getName()
                    + "和" + methodHandlerRepository.get(key).getBean().getClass() + "." + methodHandlerRepository.get(key).getMethod().getName());
        }
        methodHandlerRepository.put(key, new MethodHandler(streamListener.queue(), bean, method));
    }

    protected String getKey(String queue, String group, String consumer) {
        return queue + ":" + group + ":" + consumer;
    }

    @Override
    public void destroy() throws Exception {
        doDestroy();
    }

    /**
     * 销毁资源
     * @throws Exception ex
     */
    abstract void doDestroy() throws Exception;

    protected  <T> void routeMessage(String queue, String group, String consumer, T message, String recordId){
        String key = getKey(queue, group, consumer);
        MethodHandler methodHandler = methodHandlerRepository.get(key);
        methodHandler.routeMessage(new StreamChannel<T>(group, message, messageHandler, queue, recordId));
    }
}
