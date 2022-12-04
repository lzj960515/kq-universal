package com.kqinfo.universal.dynamic.test;

import com.kqinfo.universal.comdao.core.CommonDao;
import com.kqinfo.universal.dynamic.datasource.DynamicDataSourceContextHolder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 测试动态切换数据源
 * @author Zijian Liao
 * @since 1.0.0
 */
@SpringBootTest
public class SwitchDataSourceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void clear(){
        DynamicDataSourceContextHolder.set(1);
        jdbcTemplate.update("delete from `user`");
        DynamicDataSourceContextHolder.remove();
        DynamicDataSourceContextHolder.set(2);
        jdbcTemplate.update("delete from `user`");
        DynamicDataSourceContextHolder.remove();
    }

    @Test
    public void testJdbcSwitch(){
        // 往test.user插入数据
        DynamicDataSourceContextHolder.set(1);
        jdbcTemplate.update("INSERT INTO `user` (`name`, `age`, `email`) VALUES ('张三', 18, '123@123.com');");
        // 查询
        List<JSONObject> jsonObjectList = query();
        Assertions.assertEquals(1, jsonObjectList.size());
        DynamicDataSourceContextHolder.remove();

        // 往test.user插入数据
        DynamicDataSourceContextHolder.set(2);
        jdbcTemplate.update("INSERT INTO `user` (`name`, `age`, `email`) VALUES ('张三', 18, '123@123.com');");
        // 查询
        List<JSONObject> jsonObjectList2 = query();
        Assertions.assertEquals(1, jsonObjectList2.size());
        DynamicDataSourceContextHolder.remove();
    }

    private List<JSONObject> query(){
        return jdbcTemplate.query("select * from `user`", new RowMapper<JSONObject>() {
            @Override
            public JSONObject mapRow(ResultSet resultSet, int i) throws SQLException {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", resultSet.getString("name"));
                    jsonObject.put("age", resultSet.getInt("age"));
                    jsonObject.put("email", resultSet.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        });
    }

    @Resource
    private CommonDao commonDao;

    @Test
    public void testCommonDaoSwitch(){
        DynamicDataSourceContextHolder.set(1);
        User user = new User();
        user.setName("张三");
        user.setAge(1);
        user.setEmail("xxxxx");
        commonDao.insert(user);
        DynamicDataSourceContextHolder.remove();
    }
}
