package com.kqinfo.universal.yapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YapiParameter {
    /**
     * 字段名称
     */
    String value();
    /**
     * mock
     */
    String mock() default "";
    /**
     * 是否必须
     */
    boolean required() default false;
    /**
     * 是否忽略
     */
    boolean hidden() default false;
}
