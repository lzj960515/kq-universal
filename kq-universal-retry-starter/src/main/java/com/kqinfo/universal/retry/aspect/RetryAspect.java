package com.kqinfo.universal.retry.aspect;

import com.alibaba.fastjson.JSON;
import com.kqinfo.universal.retry.annotation.Retry;
import com.kqinfo.universal.retry.constant.StatusEnum;
import com.kqinfo.universal.retry.context.RetryContext;
import com.kqinfo.universal.retry.domain.RetryRecord;
import com.kqinfo.universal.retry.service.RetryRecordService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
@Aspect
public class RetryAspect {

    @Resource
    private RetryRecordService retryRecordService;

    @Around(value = "@annotation(retry)")
    public Object around(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            // 如果是重试调用，直接抛出异常
            if(RetryContext.isRetry()){
                throw e;
            }
            // 如果是需要重试的异常并且不在忽略的异常中
            if (isRetryEx(retry, e) && !isNoRetryEx(retry, e)) {
                // 记录重试
                saveRetryRecord(joinPoint, retry, e);
            }else {
                // 其他情况将异常抛出
                throw e;
            }
        }
        return null;
    }

    private boolean isRetryEx(Retry retry, Throwable e) {
        // 如果没有配置，则表示任何异常都要重试
        if (retry.retryFor().length == 0) {
            return true;
        }
        for (Class<? extends Throwable> retryClass : retry.retryFor()) {
            if (match(e.getClass(), retryClass.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNoRetryEx(Retry retry, Throwable e) {
        for (Class<? extends Throwable> noRetryClass : retry.noRetryFor()) {
            if (match(e.getClass(), noRetryClass.getName())) {
                return true;
            }
        }
        return false;
    }

    private void saveRetryRecord(ProceedingJoinPoint joinPoint, Retry retry, Throwable e) throws InvocationTargetException, IllegalAccessException {
        // 取出类名，方法名，参数列表
        Class<?> targetClass = joinPoint.getTarget().getClass();
        final String beanName = Introspector.decapitalize(targetClass.getSimpleName());
        // 方法名
        final Method method = resolveMethod(joinPoint);
        // 参数列表
        final Object[] args = joinPoint.getArgs();
        final List<Object> parameter = Arrays.asList(args);

        RetryRecord retryRecord = new RetryRecord();
        retryRecord.setBeanName(beanName);
        retryRecord.setMethod(method.getName());
        retryRecord.setParameter(JSON.toJSONString(parameter));
        retryRecord.setFailReason(e.getMessage());
        retryRecord.setRetryTimes(0);
        retryRecord.setMaxRetryTimes(retry.maxRetryTimes());
        final int minutes = retry.fixedRate();
        retryRecord.setFixedRate(minutes);
        final LocalDateTime nextTime = LocalDateTime.now().plusMinutes(minutes);
        retryRecord.setNextTime(nextTime);
        retryRecord.setStatus(StatusEnum.EXECUTING.status());

        retryRecordService.record(retryRecord);
    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
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

    private boolean match(Class<?> exceptionClass, String exceptionName) {
        if (exceptionClass.getName().equals(exceptionName)) {
            // Found it!
            return true;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionClass == Throwable.class) {
            return false;
        }
        return match(exceptionClass.getSuperclass(), exceptionName);
    }
}
