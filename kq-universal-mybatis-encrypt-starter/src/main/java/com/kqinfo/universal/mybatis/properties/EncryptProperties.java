package com.kqinfo.universal.mybatis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "kq.encrypt")
public class EncryptProperties {

    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
