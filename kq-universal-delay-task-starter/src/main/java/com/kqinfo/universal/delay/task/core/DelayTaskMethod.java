package com.kqinfo.universal.delay.task.core;

import java.lang.reflect.Method;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class DelayTaskMethod {

    private final Object bean;

    private final Method method;

    public DelayTaskMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }
}
