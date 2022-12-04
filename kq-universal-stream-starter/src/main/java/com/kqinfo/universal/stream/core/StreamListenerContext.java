package com.kqinfo.universal.stream.core;

import com.kqinfo.universal.stream.annotation.StreamListener;
import com.kqinfo.universal.stream.util.MessageHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class StreamListenerContext implements ApplicationListener<ApplicationStartedEvent>, DisposableBean, EnvironmentAware {

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
        String queue = this.findQueue(streamListener.queue());
        String key = getKey(queue, streamListener.group(), streamListener.consumer());
        MethodHandler methodHandler = methodHandlerRepository.get(key);
        if (methodHandler != null) {
            throw new RuntimeException("队列{" + key + "}重复于 " + bean.getClass() + "." + method.getName()
                    + "和" + methodHandler.getBean().getClass() + "." + methodHandler.getMethod().getName());
        }
        methodHandlerRepository.put(key, new MethodHandler(queue, bean, method));
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

    private Environment environment;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    protected String findQueue(String queue){
        if(queue.startsWith("${") && queue.endsWith("}")){
            return environment.getRequiredProperty(queue.substring(2, queue.length() - 1));
        }
        return queue;
    }
}
