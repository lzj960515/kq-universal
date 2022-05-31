package com.kqinfo.universal.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis 配置
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "kq.redis")
public class KqRedisProperties {

    /**
     * key前缀, 用于区分不同业务
     */
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
