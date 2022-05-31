package com.kqinfo.universal.retry.context;

import org.springframework.core.NamedThreadLocal;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
public class RetryContext {

    private static final NamedThreadLocal<Boolean> RETRY_CONTEXT = new NamedThreadLocal<>("retry context");

    public static Boolean isRetry(){
        return Boolean.TRUE.equals(RETRY_CONTEXT.get());
    }

    public static void setRetry(){
        RETRY_CONTEXT.set(Boolean.TRUE);
    }

    public static void remove(){
        RETRY_CONTEXT.remove();
    }
}
