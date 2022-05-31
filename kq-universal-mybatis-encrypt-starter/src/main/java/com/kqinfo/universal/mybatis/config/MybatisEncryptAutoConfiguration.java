package com.kqinfo.universal.mybatis.config;

import com.kqinfo.universal.mybatis.interceptor.FieldDecryptInterceptor;
import com.kqinfo.universal.mybatis.interceptor.FieldEncryptInterceptor;
import com.kqinfo.universal.mybatis.properties.EncryptProperties;
import com.kqinfo.universal.mybatis.util.EncryptHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Configuration
@Import({EncryptHandler.class, FieldEncryptInterceptor.class, FieldDecryptInterceptor.class})
@EnableConfigurationProperties(EncryptProperties.class)
public class MybatisEncryptAutoConfiguration {
}
