package com.kqinfo.universal.stream.core;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法执行器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class MethodHandler {

    private final String key;

    private final Object bean;

    private final Method method;

    public void routeMessage(Object redisChannel){
        try {
            method.invoke(bean, redisChannel);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("类：{}, 方法：{}，执行失败，原因：", bean.getClass().getCanonicalName(), method.getName(), e);
        }
    }

}
