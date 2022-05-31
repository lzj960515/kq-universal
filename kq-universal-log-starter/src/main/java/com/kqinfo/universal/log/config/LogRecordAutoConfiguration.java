package com.kqinfo.universal.log.config;

import com.kqinfo.universal.log.aspect.LogAspect;
import com.kqinfo.universal.log.service.DefaultLogRecordServiceImpl;
import com.kqinfo.universal.log.service.LogRecordService;
import com.kqinfo.universal.log.service.OperatorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
@Configuration
public class LogRecordAutoConfiguration {

    @Bean
    @ConditionalOnBean(OperatorService.class)
    public LogAspect logAspect(){
        return new LogAspect();
    }

    @Bean
    @ConditionalOnBean(OperatorService.class)
    @ConditionalOnMissingBean(LogRecordService.class)
    public LogRecordService logRecordService(){
        return new DefaultLogRecordServiceImpl();
    }
}
