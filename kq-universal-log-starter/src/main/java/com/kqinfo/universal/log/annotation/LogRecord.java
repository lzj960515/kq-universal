package com.kqinfo.universal.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解，实现优雅记录日志
 * @author Zijian Liao
 * @since 1.11.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

    /**
     * 日志模板，可编辑Spel表达式
     */
    String template();

    /**
     * 日志种类，使用者可根据日志种类查询日志
     */
    String category() default "";

    /**
     * 业务编号，方便使用者通过日志种类+业务编号定位唯一一系列的日志
     * 支持spel表达式
     */
    String businessNo() default "";

    /**
     * 操作人
     */
    String operator() default "";

    /**
     * 方法参数
     */
    String param() default "";
}
