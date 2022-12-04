package com.kqinfo.universal.dynamic.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Zijian Liao
 * @since 2.20.0
 */
@ConfigurationProperties(prefix = "kq.dynamic-datasource")
public class DynamicDataSourceProperties {

    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
