package com.kqinfo.universal.dynamic.test;

import com.kqinfo.universal.dynamic.datasource.DataSourceRegister;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 反复添加数据源测试
 * @author Zijian Liao
 * @since 1.0.0
 */
@SpringBootTest
public class AddAndDelDataSourceTest {

    @Resource
    private DataSourceRegister dataSourceRegister;

    @Test
    public void testAddAndDel() {
        dataSourceRegister.updatePassword("root", "root", 1);
        dataSourceRegister.updatePassword("root", "root", 1);
        dataSourceRegister.updatePassword("root", "root", 1);
        dataSourceRegister.updatePassword("root", "root", 1);
        dataSourceRegister.updatePassword("root", "root", 1);
    }
}
