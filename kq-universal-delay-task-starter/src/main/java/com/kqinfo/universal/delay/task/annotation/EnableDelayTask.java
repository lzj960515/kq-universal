package com.kqinfo.universal.delay.task.annotation;

import com.kqinfo.universal.delay.task.config.DelayTaskAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(DelayTaskAutoConfiguration.class)
public @interface EnableDelayTask {
}
