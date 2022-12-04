package com.kqinfo.universal.dynamic.config;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.kqinfo.universal.dynamic.datasource.DynamicDataSource;
import com.kqinfo.universal.dynamic.datasource.EncryptHandler;
import com.kqinfo.universal.dynamic.properties.DynamicDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

/**
 * @author Zijian Liao
 * @since 2.20.0
 */
@ComponentScan("com.kqinfo.universal.dynamic")
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class KqDynamicDataSourceAutoConfiguration {

    /**
     * 自定义动态数据源
     * @param properties 项目的数据源配置
     * @return DynamicDataSource
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource",
            matchIfMissing = true)
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return new DynamicDataSource(dataSource);
    }
    @SuppressWarnings("unchecked")
    protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
        return (T) properties.initializeDataSourceBuilder().type(type).build();
    }

    @Bean
    public EncryptHandler encryptHandler(DynamicDataSourceProperties dynamicDataSourceProperties){
        return new EncryptHandler(new SymmetricCrypto(SymmetricAlgorithm.AES, dynamicDataSourceProperties.getSecret().getBytes(StandardCharsets.UTF_8)));
    }
}
