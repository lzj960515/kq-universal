package com.kqinfo.universal.dynamic.test;

import com.kqinfo.universal.dynamic.datasource.DataSourceRegister;
import com.kqinfo.universal.dynamic.datasource.DynamicDataSourceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Rollback
@Transactional
@SpringBootTest
public class DataSourceRegisterTest {

    @Resource
    private DataSourceRegister dataSourceRegister;

    @Test
    public void testSave(){
        DynamicDataSourceInfo dynamicDataSourceInfo = new DynamicDataSourceInfo();
        dynamicDataSourceInfo.setName("测试数据源xx");
        dynamicDataSourceInfo.setDriverClassName("");
        dynamicDataSourceInfo.setJdbcUrl("jdbc:mysql://192.168.65.206:3306/test3?serverTimezone=Asia/Shanghai&useLegacyDatetimeCode=false&nullNamePatternMatchesAll=true&zeroDateTimeBehavior=CONVERT_TO_NULL&tinyInt1isBit=false&autoReconnect=true&useSSL=false&pinGlobalTxToPhysicalConnection=true&characterEncoding=utf8");
        dynamicDataSourceInfo.setUsername("root");
        dynamicDataSourceInfo.setPassword("root");
        dataSourceRegister.putDataSource(dynamicDataSourceInfo);
        DynamicDataSourceInfo returnValue = dataSourceRegister.get(dynamicDataSourceInfo.getId());
        Assertions.assertNotNull(returnValue);
    }

    @Test
    public void testList(){
        dataSourceRegister.deleteById(1);

        List<DynamicDataSourceInfo> list = dataSourceRegister.list();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void testUpdatePassword(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            dataSourceRegister.updatePassword("root", "1234", 1);
        });

        dataSourceRegister.updatePassword("root", "root", 1);
    }

    @Test
    public void testUpdate(){
        DynamicDataSourceInfo dynamicDataSourceInfo = dataSourceRegister.get(1);
        dynamicDataSourceInfo.setName("测试数据源xx");
        dataSourceRegister.updateById(dynamicDataSourceInfo);
        Assertions.assertThrows(RuntimeException.class, () -> {
            dynamicDataSourceInfo.setName("测试数据源xx");
            dynamicDataSourceInfo.setUsername("abcd");
            dataSourceRegister.updateById(dynamicDataSourceInfo);
        });

    }

}
