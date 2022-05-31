package com.kqinfo.universal.stream.core;

import com.kqinfo.universal.stream.annotation.StreamListener;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * RedisListener注解 扫描器
 *
 * @author Zijian Liao
 * @since 1.4.0
 */
public class StreamListenerScanner {


    public static void scan(ApplicationContext applicationContext, StreamListenerContext streamListenerContext){
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Map<Method, StreamListener> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<StreamListener>) method -> AnnotatedElementUtils.findMergedAnnotation(method, StreamListener.class));
            annotatedMethods.forEach((method, streamListener) -> {
                if(streamListener == null){
                    return;
                }
                Type genericParameterType = method.getGenericParameterTypes()[0];
                ParameterizedType parameterizedType = (ParameterizedType) genericParameterType;
                streamListenerContext.registerConsumer(streamListener, bean, method, (Class<?>) parameterizedType.getActualTypeArguments()[0]);
            });
        }
    }
}
