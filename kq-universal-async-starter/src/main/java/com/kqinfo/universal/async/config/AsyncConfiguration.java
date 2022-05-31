package com.kqinfo.universal.async.config;

import com.kqinfo.universal.async.properties.AsyncProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * 异步配置
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
@EnableAsync
@EnableConfigurationProperties(AsyncProperties.class)
public class AsyncConfiguration implements AsyncConfigurer {

    @Resource
    private AsyncProperties asyncProperties;

    @SneakyThrows
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        executor.setAllowCoreThreadTimeOut(asyncProperties.isAllowCoreThreadTimeOut());
        executor.setRejectedExecutionHandler(asyncProperties.getRejectedExecutionHandler().newInstance());
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> log.error("异步线程执行失败：objects:{} method:{}", objects, method, throwable);
    }
}
