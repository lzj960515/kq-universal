package com.kqinfo.universal.yapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller注解
 *
 * @author Zijian Liao
 * @since 2.13.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface YapiApi {
    /**
     * 是否忽略
     */
    String value();

    boolean hidden() default false;
}
