package com.kqinfo.universal.imagecode.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 图片验证码配置类
 *
 * @author Zijian Liao
 * @since 1.5.0
 */
@Data
@ConfigurationProperties(prefix = "kq.image-code")
public class ImageCodeProperties {
    /**
     * 图片的宽度
     */
    private int width = 120;
    /**
         * 图片的高度
     */
    private int height = 40;
    /**
     * 验证码字符个数
     */
    private int codeCount = 4;
    /**
     * 验证码干扰线数
     */
    @Deprecated
    private int lineCount = 100;
    /**
     * 验证码过期时间
     */
    private int codeExpireSeconds = 60;

    /**
     * 验证码基础字符串
     */
    private String baseChars = "2345678";
}
