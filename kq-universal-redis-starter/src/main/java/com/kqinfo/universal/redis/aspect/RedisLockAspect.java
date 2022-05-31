package com.kqinfo.universal.redis.aspect;

import com.kqinfo.universal.redis.annotation.RedisLock;
import com.kqinfo.universal.redis.enums.LockStrategy;
import com.kqinfo.universal.redis.exception.RedisLockException;
import com.kqinfo.universal.redis.lock.LockFactory;
import com.kqinfo.universal.redis.properties.KqRedisProperties;
import com.kqinfo.universal.redis.util.LockContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * redis lock 切面
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Aspect
public class RedisLockAspect {

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private KqRedisProperties redisProperties;

    @Around(value = "@annotation(redisLock)")
    public Object lock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        // 拼接出key
        String key = getRedisKey(joinPoint, redisLock);
        // 获取锁
        RLock lock = LockFactory.createLock(redissonClient, redisLock.lockType(), key);
        try {
            // 根据策略加锁
            redisLock.lockStrategy().lock(lock, redisLock);
            // 兼容不抛出异常逻辑
            if(LockStrategy.SKIP_AND_RETURN_NULL.equals(redisLock.lockStrategy())){
                Boolean locked = LockContext.getAndRemove();
                // locked为null, 表示加锁失败，直接返回null
                if(locked == null){
                    return null;
                }
            }
            return joinPoint.proceed();
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String getRedisKey(ProceedingJoinPoint joinPoint, RedisLock redisLock){
        EvaluationContext context = new MethodBasedEvaluationContext(TypedValue.NULL, resolveMethod(joinPoint), joinPoint.getArgs(), parameterNameDiscoverer);
        StringBuilder sb = new StringBuilder();
        ExpressionParser parser = new SpelExpressionParser();
        for (String key : redisLock.keys()) {
            // keys是个spel表达式
            Expression expression = parser.parseExpression(key);
            Object value = expression.getValue(context);
            sb.append(ObjectUtils.nullSafeToString(value));
        }
        if(StringUtils.hasText(redisProperties.getPrefix())){
            return redisProperties.getPrefix() + redisLock.name() + sb;
        }
        return redisLock.name() + sb;
    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        return getDeclaredMethodFor(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
    }

    private Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, name, parameterTypes);
            }
        }
        throw new IllegalStateException("Cannot resolve target method: " + name);
    }

}
