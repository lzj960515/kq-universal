package com.kqinfo.universal.async.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 异步配置类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "kq.async")
public class AsyncProperties {
    
    private Integer corePoolSize = 20;

    private Integer maxPoolSize = 100;

    private Integer keepAliveSeconds = 60;

    private Integer queueCapacity = 200;

    private boolean allowCoreThreadTimeOut = false;

    private String threadNamePrefix = "kq-async-";

    private Class<? extends RejectedExecutionHandler> rejectedExecutionHandler = java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.class;

}
