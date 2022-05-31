package com.kqinfo.universal.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * swagger配置
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "kq.swagger")
public class SwaggerProperties {

    private String title;

    private String description;

    private String basePackage;
}
