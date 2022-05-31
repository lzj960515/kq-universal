package com.kqinfo.universal.delay.task.test;

import com.kqinfo.universal.delay.task.core.domain.DelayTaskInfo;
import com.kqinfo.universal.delay.task.dao.DelayTaskDao;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
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
public class DelayTaskDaoTest {

    @Resource
    private DelayTaskDao delayTaskDao;

    @Test
    public void testSave(){
        DelayTaskInfo delayTaskInfo = new DelayTaskInfo();
        delayTaskInfo.setName("测试dao");
        delayTaskInfo.setDescription("描述");
        delayTaskInfo.setInfo("信息");
        delayTaskInfo.setExecuteTime(System.currentTimeMillis());
        delayTaskInfo.setExecuteStatus(1);
        delayTaskInfo.setExecuteMessage("message");
        delayTaskDao.save(delayTaskInfo);
        MatcherAssert.assertThat(delayTaskInfo.getId(), CoreMatchers.notNullValue());
    }

    @Sql("/test.sql")
    @Test
    public void testFind(){
        final DelayTaskInfo delayTaskInfo = delayTaskDao.findById(1L);
        MatcherAssert.assertThat(delayTaskInfo, CoreMatchers.notNullValue());

    }

    @Sql("/test.sql")
    @Test
    public void testFindByExecuteTime(){
        List<DelayTaskInfo> delayTaskInfos = delayTaskDao.findByExecuteTime(System.currentTimeMillis());
        MatcherAssert.assertThat(delayTaskInfos.size(), CoreMatchers.is(2));
    }

    @Sql("/test.sql")
    @Test
    public void testDelete(){
        delayTaskDao.deleteByExecuteTime(System.currentTimeMillis());
    }
}
