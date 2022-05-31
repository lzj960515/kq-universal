package com.kqinfo.universal.retry.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 消息配置
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "kq.retry")
public class AlarmProperties {

    /**
     * 重试失败后告知的邮件地址
     */
    private List<String> mailAddress;
}
