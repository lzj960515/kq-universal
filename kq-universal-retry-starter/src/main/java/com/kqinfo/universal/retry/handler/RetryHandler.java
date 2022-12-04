package com.kqinfo.universal.retry.handler;

import com.alibaba.fastjson.JSON;
import com.kqinfo.universal.retry.context.RetryContext;
import com.kqinfo.universal.retry.domain.RetryRecord;
import com.kqinfo.universal.retry.service.RetryAlarmService;
import com.kqinfo.universal.retry.service.RetryRecordService;
import com.kqinfo.universal.retry.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
@Slf4j
public class RetryHandler {

    @Resource
    private RetryRecordService retryRecordService;
    @Resource
    private RetryAlarmService retryAlarmService;


    public void execute() {
        // 查询需要重试的记录
        final List<RetryRecord> retryRecords = retryRecordService.listExecutingRecord();
        // 进行重试
        final LocalDateTime now = LocalDateTime.now();
        RetryContext.setRetry();
        for (RetryRecord retryRecord : retryRecords) {
            final LocalDateTime nextTime = retryRecord.getNextTime();
            // 当前时间在下次重试时间之后，可以重试
            if(now.isAfter(nextTime)){
                retry(retryRecord);
            }
        }
        RetryContext.remove();
    }

    private void retry(RetryRecord retryRecord) {
        try {
            doRetry(retryRecord);
            retryRecordService.updateRecordSuccess(retryRecord.getId());
        }catch (InvocationTargetException e){
            log.error(e.getMessage(), e);
            final Throwable targetException = e.getTargetException();
            handleFail(retryRecord, targetException);
        }catch (Throwable e){
            handleFail(retryRecord, e);
        }
    }

    private void handleFail(RetryRecord retryRecord, Throwable e){
        final boolean fail = isFail(retryRecord.getRetryTimes(), retryRecord.getMaxRetryTimes());
        retryRecordService.updateRetryTimes(retryRecord.getId(), fail, e.getMessage(), retryRecord.getFixedRate());
        if(fail){
            retryAlarmService.alarm(retryRecord);
        }
    }

    private boolean isFail(Integer retryTimes, Integer maxRetryTimes){
        // 重试次数为之前的重试次数，加上本次，等于最大重试次数
        return retryTimes + 1 >= maxRetryTimes;
    }

    private void doRetry(RetryRecord retryRecord) throws InvocationTargetException, IllegalAccessException {
        final String beanName = retryRecord.getBeanName();
        final String method = retryRecord.getMethod();
        final String parameter = retryRecord.getParameter();
        Object retryService = SpringUtil.getBean(beanName);
        Class<?> retryServiceClass = retryService.getClass();

        Method retryMethod = chooseMethod(retryServiceClass, method);

        Type[] parameterTypes = retryMethod.getGenericParameterTypes();
        final List<Object> list = JSON.parseArray(parameter, Object.class);
        List<Object> args = new ArrayList<>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            final Object object = JSON.parseObject(JSON.toJSONString(list.get(i)), parameterTypes[i]);
            args.add(object);
        }
        final Object[] objects = args.toArray();
        retryMethod.invoke(retryService, objects);
    }

    private Method chooseMethod(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (methodName.equals(name)) {
                return method;
            }
        }
        throw new IllegalStateException("Cannot resolve target method: " + methodName);
    }
}
