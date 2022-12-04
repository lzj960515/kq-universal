package com.kqinfo.universal.cache.config;

import com.kqinfo.universal.cache.core.KqCacheAsyncHandler;
import com.kqinfo.universal.cache.core.KqCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zijian Liao
 * @since cache
 */
@Configuration
public class KqCacheAutoConfiguration {

    @Bean
    public KqCacheManager kqCacheManager(){
        return new KqCacheManager();
    }

    @Bean
    public KqCacheAsyncHandler kqCacheAsyncHandler(){
        return new KqCacheAsyncHandler();
    }
}
