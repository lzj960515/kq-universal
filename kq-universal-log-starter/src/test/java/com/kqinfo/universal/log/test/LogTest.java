package com.kqinfo.universal.log.test;

import com.kqinfo.universal.log.domain.OperateLog;
import com.kqinfo.universal.log.result.PageResult;
import com.kqinfo.universal.log.service.LogRecordService;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogTest {

    @Resource
    private TestService testService;

    @Resource
    private LogRecordService logRecordService;

    @Test
    public void test(){
        Order order = new Order();
        order.setId("1234");
        testService.test(order);
    }

    @Test
    public void page(){
        final PageResult<OperateLog> orderLogs = logRecordService.page(1, 10, "order", "1234");
        final List<OperateLog> records = orderLogs.getRecords();
        assertThat(records.get(0).getBusinessNo(), CoreMatchers.is("1234"));
    }

    @Test
    public void testGetById(){
        OperateLog operateLog = logRecordService.getById(11L);
        assertThat(operateLog, CoreMatchers.notNullValue());
    }

}
