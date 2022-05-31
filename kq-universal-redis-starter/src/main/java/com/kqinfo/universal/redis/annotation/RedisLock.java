package com.kqinfo.universal.redis.annotation;

import com.kqinfo.universal.redis.enums.LockStrategy;
import com.kqinfo.universal.redis.enums.LockType;
import com.kqinfo.universal.redis.exception.RedisLockFailFastException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis lock
 * 对加上该注解的方法上锁
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    /**
     * key的名称前缀，与keys字段共同拼接出一个redis key
     * 如 name = user keys = 123（userId）
     * 最终使用的key为 user123
     */
    String name();

    /**
     * 能够确定出系统中唯一性资源的key
     * 如 用户， 使用用户id为key
     * 或者使用 用户名+手机号
     * 必须为spel表达式 如 #id #user.id #user.name
     */
    String[] keys();

    /**
     * 锁的类型，默认为可重入锁
     *
     * @see LockType
     */
    LockType lockType() default LockType.Lock;

    /**
     * 加锁策略，默认加锁不成功就抛出异常 {@link RedisLockFailFastException}
     *
     * @see LockStrategy
     */
    LockStrategy lockStrategy() default LockStrategy.FAIL_FAST;

    /**
     * 尝试获取锁的超时时间(秒)，默认30s
     * 当加锁策略为 {@link LockStrategy.KEEP_ACQUIRE_TIMEOUT} 时有效
     * -1表示一直等待，等同于{@link LockStrategy.KEEP_ACQUIRE} 策略
     */
    long waitTime() default 30;

    /**
     * 锁的过期时间(秒)，默认30s过期
     * -1表示永不过期（逻辑上的永不过期，实际上过期为30s, 在过期时间剩余20s时将自动续期到30s）
     */
    long leaseTime() default 30;

    /**
     * 自定义异常消息
     */
    String exceptionMessage() default "";
}
