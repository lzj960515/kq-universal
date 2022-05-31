package com.kqinfo.universal.retry.config;

import com.kqinfo.universal.retry.aspect.RetryAspect;
import com.kqinfo.universal.retry.handler.RetryHandler;
import com.kqinfo.universal.retry.properties.AlarmProperties;
import com.kqinfo.universal.retry.service.DefaultRetryAlarmServiceImpl;
import com.kqinfo.universal.retry.service.DefaultRetryRecordServiceImpl;
import com.kqinfo.universal.retry.service.RetryAlarmService;
import com.kqinfo.universal.retry.service.RetryRecordService;
import com.kqinfo.universal.retry.util.MailUtil;
import com.kqinfo.universal.retry.util.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Import(SpringUtil.class)
@EnableConfigurationProperties(AlarmProperties.class)
public class RetryAutoConfiguration {

    @Bean
    public RetryAspect retryAspect(){
        return new RetryAspect();
    }

    @ConditionalOnMissingBean(RetryRecordService.class)
    @Bean
    public RetryRecordService retryRecordService(){
        return new DefaultRetryRecordServiceImpl();
    }

    @ConditionalOnMissingBean(RetryAlarmService.class)
    @Bean
    public RetryAlarmService retryAlarmService(){
        return new DefaultRetryAlarmServiceImpl();
    }

    @Bean
    public RetryHandler retryHandler(){
        return new RetryHandler();
    }

    @Bean
    public MailUtil mailUtil(){
        return new MailUtil();
    }
}
