package com.kqinfo.universal.comdao.test;

import cn.hutool.core.bean.BeanUtil;
import com.kqinfo.universal.comdao.core.CommonDaoWrap;
import com.kqinfo.universal.comdao.core.PageResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Rollback
@Transactional
@SpringBootTest
public class CommonDaoWrapTest {

    @Resource
    private CommonDaoWrap commonDaoWrap;

    @Test
    public void testInsert() {
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        user.setCreateTime(LocalDateTime.now().plusYears(-18));
        int insert = commonDaoWrap.insert(user);
        Assertions.assertEquals(1, insert);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testUpdate() {
        User user = new User();
        user.setName("李四");
        user.setAge(17);
        user.setId(1L);
        int update = commonDaoWrap.update(user);
        Assertions.assertEquals(1, update);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testSelect() {
        User user = commonDaoWrap.select(1L, User.class);
        Assertions.assertEquals("李四", user.getName());
        Assertions.assertEquals(17, user.getAge());
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testDelete() {
        int delete = commonDaoWrap.delete(1L, User.class);
        Assertions.assertEquals(1, delete);
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectCount() {
        User user = new User();
        user.setAge(18);
        long count = commonDaoWrap.selectCount(user);
        Assertions.assertEquals(2, count);
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectList() {
        User user = new User();
        user.setAge(18);
        List<User> maps = commonDaoWrap.selectList(user);
        Assertions.assertEquals(2, maps.size());
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testSelectOne() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("age", 17);
        User user = commonDaoWrap.selectOne("select * from user where age = #{age}", params, User.class);
        Assertions.assertEquals("李四", user.getName());
        Assertions.assertEquals(17, user.getAge());
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testSelectByUnique() {
        User param = new User();
        param.setAge(17);
        User user = commonDaoWrap.selectByUnique(param);
        Assertions.assertEquals("李四", user.getName());
        Assertions.assertEquals(17, user.getAge());
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectCountBySQL() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("age", 18);
        long count = commonDaoWrap.selectCountBySQL("select count(*) from user where age = #{age}", params);
        Assertions.assertEquals(2, count);
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectListBySql() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("age", 18);
        List<User> users = commonDaoWrap.selectListBySql("select * from user where age = #{age}", params, User.class);
        Assertions.assertEquals(2, users.size());
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testUpdateSql() {
        User user = new User();
        user.setName("张三");
        user.setId(1L);
        Map<String, Object> params = BeanUtil.beanToMap(user);
        long update = commonDaoWrap.updateSql("update user set name = #{name} where id = #{id}", params);
        Assertions.assertEquals(1, update);
    }

    @Sql("/sql/test_crud.sql")
    @Test
    public void testDeleteSql() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("id", 1);
        long delete = commonDaoWrap.deleteSql("delete from user where id = #{id}", params);
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
        int insert = commonDaoWrap.insertBatch(userList);
        Assertions.assertEquals(2, insert);
    }

    @Sql("/sql/test_batch.sql")
    @Test
    public void testDeleteBatch() {
        int delete = commonDaoWrap.deleteBatch(new Long[]{1L, 2L}, User.class);
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
        int replace = commonDaoWrap.replace(user);
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
        int replace = commonDaoWrap.replaceBatch(Collections.singletonList(user));
        Assertions.assertEquals(2, replace);
    }

    @Sql("/sql/test_select.sql")
    @Test
    public void testSelectPageList() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("age", 18);
        PageResult<User> userPageResult = commonDaoWrap.selectPageList("select *", "from user where age = #{age}", params, User.class, 1, 10, "order by create_time");
        Assertions.assertEquals(2, userPageResult.getList().size());
    }


}