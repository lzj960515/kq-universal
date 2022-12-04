package com.kqinfo.universal.dynamic.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zijian Liao
 * @since 2.20.0
 */
@Slf4j
public class DynamicDataSource extends AbstractDataSource {


    @Lazy
    @Autowired
    private DataSourceRegister dataSourceRegister;

    @Resource
    private DataSourceHelper dataSourceHelper;


    private final DataSource defaultDatasource;

    public DynamicDataSource(DataSource dataSource){
        this.defaultDatasource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineDataSource().getConnection(username, password);
    }

    private DataSource determineDataSource() {
        Integer dsId = DynamicDataSourceContextHolder.get();
        if(dsId == null){
            return defaultDatasource;
        }
        // 从缓存数据源中获取
        DataSource dataSource = dataSourceHelper.getDataSource(dsId);
        if(dataSource != null){
            return dataSource;
        }
        // 创建数据源
        DynamicDataSourceInfo dynamicDataSourceInfo = dataSourceRegister.get(dsId);
        if (dynamicDataSourceInfo == null){
            return defaultDatasource;
        }
        DataSource hikariDataSource = dataSourceHelper.createDataSource(dynamicDataSourceInfo);
        dataSourceHelper.putDataSource(dsId, hikariDataSource);
        return hikariDataSource;
    }


}
