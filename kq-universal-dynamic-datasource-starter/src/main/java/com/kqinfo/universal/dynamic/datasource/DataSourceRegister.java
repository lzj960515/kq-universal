package com.kqinfo.universal.dynamic.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.20.0
 */
@Component
public class DataSourceRegister implements InitializingBean {

    @Resource
    private DynamicDataSourceDao dynamicDataSourceDao;
    @Resource
    private DataSourceHelper dataSourceHelper;


    public void putDataSource(DynamicDataSourceInfo dynamicDataSourceInfo){
        dynamicDataSourceInfo.setPassword(dataSourceHelper.encryptPassword(dynamicDataSourceInfo.getPassword()));
        // 检查数据源
        DataSource dataSource = dataSourceHelper.checkValid(dynamicDataSourceInfo);
        dynamicDataSourceDao.save(dynamicDataSourceInfo);
        dataSourceHelper.putDataSource(dynamicDataSourceInfo.getId(), dataSource);
    }

    public void deleteById(Integer id){
        dynamicDataSourceDao.deleteById(id);
        dataSourceHelper.removeDataSource(id);
    }

    public void updateById(DynamicDataSourceInfo dynamicDataSourceInfo){
        // 检查数据源
        DataSource dataSource = dataSourceHelper.checkValid(dynamicDataSourceInfo);
        dynamicDataSourceDao.updateById(dynamicDataSourceInfo);
        dataSourceHelper.putDataSource(dynamicDataSourceInfo.getId(), dataSource);
    }

    public void updatePassword(String username, String password, Integer id){
        DynamicDataSourceInfo dynamicDataSourceInfo = this.get(id);
        dynamicDataSourceInfo.setUsername(username);
        dynamicDataSourceInfo.setPassword(dataSourceHelper.encryptPassword(password));
        DataSource dataSource = dataSourceHelper.checkValid(dynamicDataSourceInfo);
        dynamicDataSourceDao.updatePassword(username, dynamicDataSourceInfo.getPassword(), id);
        dataSourceHelper.putDataSource(id, dataSource);
    }

    public List<DynamicDataSourceInfo> list(){
        return dynamicDataSourceDao.list();
    }

    public DynamicDataSourceInfo get(Integer id){
        return dynamicDataSourceDao.getById(id);
    }

    public void reconnect(Integer id){
        DynamicDataSourceInfo dynamicDataSourceInfo = this.get(id);
        HikariDataSource dataSource = dataSourceHelper.createDataSource(dynamicDataSourceInfo);
        dataSourceHelper.putDataSource(id, dataSource);
    }

    @Override
    public void afterPropertiesSet() {
        List<DynamicDataSourceInfo> list = dynamicDataSourceDao.list();
        for (DynamicDataSourceInfo dynamicDataSourceInfo : list) {
            // 检查数据源是否可以连接
            DataSource dataSource = dataSourceHelper.isValid(dynamicDataSourceInfo);
            if (dataSource != null) {
                dataSourceHelper.putDataSource(dynamicDataSourceInfo.getId(), dataSource);
            }
        }
    }

}
