package com.kqinfo.universal.redis.util;

import org.springframework.core.NamedThreadLocal;

/**
 * @author Zijian Liao
 * @since 2.3.0
 */
public class LockContext {

    private static final NamedThreadLocal<Boolean> context = new NamedThreadLocal<>("lock context");

    public static Boolean getAndRemove(){
        Boolean b = context.get();
        context.remove();
        return b;
    }

    public static void setSuccess(){
        context.set(Boolean.TRUE);
    }

}
