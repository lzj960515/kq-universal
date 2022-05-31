package com.kqinfo.universal.retry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对加上该注解的方法进行重试
 * 注意：该方法应是一个没有返回值的方法，如果有返回值，那么这必定是个同步方法，出现异常事务必然回滚，重试是没有意义的
 * @author Zijian Liao
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    /**
     * 指定需要重试的异常
     * 当方法抛出指定的异常时，保存重试记录
     */
    Class<? extends Throwable>[] retryFor() default {};

    /**
     * 指定不重试的异常
     * 当方法抛出的异常在不重试异常之中时，将不再进行保存重试记录，会将该异常往外抛出
     */
    Class<? extends Throwable>[] noRetryFor() default {};

    /**
     * 重试间隔，单位分钟
     */
    int fixedRate() default 10;

    /**
     * 最大重试次数
     */
    int maxRetryTimes() default 5;
}
