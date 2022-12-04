package com.kqinfo.universal.yapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口注解
 * @author Zijian Liao
 * @since 2.13.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface YapiOperation {

    String value();
    /**
     * 是否忽略
     */
    boolean hidden() default false;
}
