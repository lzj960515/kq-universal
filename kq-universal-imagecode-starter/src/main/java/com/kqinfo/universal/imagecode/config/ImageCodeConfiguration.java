package com.kqinfo.universal.imagecode.config;

import com.kqinfo.universal.imagecode.core.ImageCodeHandler;
import com.kqinfo.universal.imagecode.core.ImageCodeUtil;
import com.kqinfo.universal.imagecode.properties.ImageCodeProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Zijian Liao
 * @since 1.5.0
 */
@EnableConfigurationProperties(ImageCodeProperties.class)
public class ImageCodeConfiguration {

    @Bean
    public ImageCodeUtil imageCodeUtil(ImageCodeProperties properties){
        return new ImageCodeUtil(properties);
    }

    @ConditionalOnMissingBean(StringRedisTemplate.class)
    @Bean
    public ImageCodeHandler imageCodeHandler(){
        return new ImageCodeHandler();
    }


}
