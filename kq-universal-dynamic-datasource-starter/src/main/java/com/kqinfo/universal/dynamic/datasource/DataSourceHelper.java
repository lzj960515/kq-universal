package com.kqinfo.universal.dynamic.datasource;

import cn.hutool.core.io.IoUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zijian Liao
 * @since 2.20.0
 */
@Slf4j
@Component
public class DataSourceHelper {

    private static final Map<Integer, DataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>(16);

    @Resource
    private EncryptHandler encryptHandler;

    public DataSource getDataSource(Integer dsId) {
        return DATA_SOURCE_MAP.get(dsId);
    }

    public void putDataSource(Integer dsId, DataSource dataSource) {
        DataSource exist = DATA_SOURCE_MAP.get(dsId);
        if(exist instanceof AutoCloseable){
            IoUtil.close((AutoCloseable) exist);
        }
        DATA_SOURCE_MAP.put(dsId, dataSource);
    }

    public void removeDataSource(Integer dsId) {
        DATA_SOURCE_MAP.remove(dsId);
    }

    public HikariDataSource createDataSource(DynamicDataSourceInfo dynamicDataSourceInfo) {
        HikariConfig hikariConfig = new HikariConfig();
        if (StringUtils.hasText(dynamicDataSourceInfo.getDriverClassName())) {
            hikariConfig.setDriverClassName(dynamicDataSourceInfo.getDriverClassName());
        }
        hikariConfig.setJdbcUrl(encryptHandler.decrypt(dynamicDataSourceInfo.getJdbcUrl()));
        hikariConfig.setUsername(encryptHandler.decrypt(dynamicDataSourceInfo.getUsername()));
        hikariConfig.setPassword(encryptHandler.decrypt(dynamicDataSourceInfo.getPassword()));
        if (hasNumber(dynamicDataSourceInfo.getMinimumIdle())) {
            hikariConfig.setMinimumIdle(dynamicDataSourceInfo.getMinimumIdle());
        }
        if (hasNumber(dynamicDataSourceInfo.getIdleTimeout())){
            hikariConfig.setIdleTimeout(dynamicDataSourceInfo.getIdleTimeout());
        }
        if (hasNumber(dynamicDataSourceInfo.getMaximumPoolSize())){
            hikariConfig.setMaximumPoolSize(dynamicDataSourceInfo.getMaximumPoolSize());
        }
        hikariConfig.setAutoCommit(true);
        if (hasNumber(dynamicDataSourceInfo.getConnectionTimeout())){
            hikariConfig.setConnectionTimeout(dynamicDataSourceInfo.getConnectionTimeout());
        }
        if (StringUtils.hasText(dynamicDataSourceInfo.getConnectionTestQuery())) {
            hikariConfig.setConnectionTestQuery(dynamicDataSourceInfo.getConnectionTestQuery());
        }
        return new HikariDataSource(hikariConfig);
    }

    /**
     * 数据统一加密
     * @param row
     * @return
     */
    public String encryptRow(String row) {
        return encryptHandler.encrypt(row);
    }

    /**
     * 数据统一解密
     * @param row
     * @return
     */
    public String decryptRow(String row) {
        return encryptHandler.decrypt(row);
    }

    public DataSource checkValid(DynamicDataSourceInfo dynamicDataSourceInfo) {
        try {
            return createDataSource(dynamicDataSourceInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("数据库连接失败");
        }
    }

    public DataSource isValid(DynamicDataSourceInfo dynamicDataSourceInfo) {
        try {
            return createDataSource(dynamicDataSourceInfo);
        } catch (Exception e) {
            log.warn("数据源[{}]尝试连接失败", dynamicDataSourceInfo.getName(), e);
            return null;
        }
    }

    private boolean hasNumber(Integer number) {
        return number != null && number != 0;
    }

}
