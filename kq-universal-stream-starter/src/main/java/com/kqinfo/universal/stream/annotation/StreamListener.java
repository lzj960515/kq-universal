package com.kqinfo.universal.stream.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zijian Liao
 * @since 1.4.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StreamListener {

    String queue();

    /**
     * 消费者组，不同的消费者组可以消费同一条消息
     */
    String group() default "group-1";

    /**
     * 消费者组中的消费者，对于一条消息，只能被一个组中的某一个消费者消费
     * 如 两个组：group1 group2
     * group1中具有3个消费者， group2中也有三个消费者
     * group1能消费这条消息，但是3个消费者中只能有一个消费者消费消息
     * group2也能消费这条消息，但是3个消费者中也只能有一个消费者消费消息
     */
    String consumer() default "consumer-1";

    /**
     * 每次拉取消息的数量
     */
    int batchSize() default 10;

    /**
     * 队列保存最大数据量
     */
    int queueMaxLen() default 10000;

}
