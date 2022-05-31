package com.kqinfo.universal.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 自动装配
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Slf4j
@ComponentScan(basePackages = "com.kqinfo.universal.common")
public class UniversalAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("universal组件加载完毕！！");
    }
}
