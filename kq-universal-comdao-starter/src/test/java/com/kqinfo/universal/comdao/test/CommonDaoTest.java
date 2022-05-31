package com.kqinfo.universal.comdao.test;

import cn.hutool.core.bean.BeanUtil;
import com.kqinfo.universal.comdao.core.CommonDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Zijian Liao
 * @since 2.8.0
 */
@Rollback
@Transactional
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommonDaoTest {

    @Resource
    private CommonDao commonDao;

    @Test
    public void testInsert() {
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        user.setCreateTime(LocalDateTime.now().plusYears(-18));
        int insert = commonDao.insert(user);
        Assertions.assertEquals(1, insert);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testUpdate() {
        User user = new User();
        user.setName("李四");
        user.setAge(17);
        user.setId(1L);
        int update = commonDao.update(user);
        Assertions.assertEquals(1, update);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testSelect() {
        Map<?, ?> map = commonDao.select(1L, User.class);
        Assertions.assertEquals("李四", map.get("name"));
        Assertions.assertEquals(17, map.get("age"));
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testDelete() {
        int delete = commonDao.delete(1L, User.class);
        Assertions.assertEquals(1, delete);
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectCount() {
        User user = new User();
        user.setAge(18);
        long count = commonDao.selectCount(user);
        Assertions.assertEquals(2, count);
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectList() {
        User user = new User();
        user.setAge(18);
        List<Map<?, ?>> maps = commonDao.selectList(user);
        Assertions.assertEquals(2, maps.size());
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectCountBySql() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("age", 18);
        params.put("selfSQL", "select count(*) from user where age = #{age}");
        long count = commonDao.selectCountBySql(params);
        Assertions.assertEquals(2, count);
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectListBySql() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("age", 18);
        params.put("selfSQL", "select * from user where age = #{age}");
        List<Map<?, ?>> maps = commonDao.selectListBySql(params);
        Assertions.assertEquals(2, maps.size());
    }

    @Test
    public void testInsertSql() {
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        user.setCreateTime(LocalDateTime.now().plusYears(-18));
        Map<String, Object> params = BeanUtil.beanToMap(user);
        params.put("selfSQL", "insert into user(name, age, create_time) values(#{name}, #{age}, #{createTime})");
        long insert = commonDao.insertSql(params);
        Assertions.assertEquals(1, insert);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testUpdateSql() {
        User user = new User();
        user.setName("张三");
        user.setId(1L);
        Map<String, Object> params = BeanUtil.beanToMap(user);
        params.put("selfSQL", "update user set name = #{name} where id = #{id}");
        long update = commonDao.updateSql(params);
        Assertions.assertEquals(1, update);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testDeleteSql() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("id", 1);
        params.put("selfSQL", "delete from user where id = #{id}");
        long delete = commonDao.deleteSql(params);
        Assertions.assertEquals(1, delete);
    }

    @Test
    public void testInsertBatch() {
        List<User> userList = new ArrayList<>();
        {
            User user = new User();
            user.setName("张三")
                    .setAge(18)
                    .setCreateTime(LocalDateTime.now());
            userList.add(user);
        }
        {
            User user = new User();
            user.setName("李四")
                    .setAge(17)
                    .setCreateTime(LocalDateTime.now());
            userList.add(user);
        }
        int insert = commonDao.insertBatch(userList);
        Assertions.assertEquals(2, insert);
    }

    @Sql("/sql/test_batch.sql")
    @Test
    public void testDeleteBatch() {
        int delete = commonDao.deleteBatch(new Long[]{1L, 2L}, User.class);
        Assertions.assertEquals(2, delete);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testReplace() {
        User user = new User();
        user.setId(1L)
                .setName("张三")
                .setAge(18)
                .setCreateTime(LocalDateTime.now());
        int replace = commonDao.replace(user);
        Assertions.assertEquals(2, replace);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testReplaceBatch() {
        User user = new User();
        user.setId(1L)
                .setName("张三")
                .setAge(18)
                .setCreateTime(LocalDateTime.now());
        int replace = commonDao.replaceBatch(Collections.singletonList(user));
        Assertions.assertEquals(2, replace);
    }
}
