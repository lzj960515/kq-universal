package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.core.WorkflowInvoker;
import com.kqinfo.universal.workflow.dto.Assignee;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import com.kqinfo.universal.workflow.exception.WorkflowException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author Zijian Liao
 * @since 2.4.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EventTest {

    private static final String EVENT = "测试事件";
    @Resource
    private WorkflowInvoker workflowInvoker;

    @Test
    public void testStart(){
        ProcessStartDto processStartDto = new ProcessStartDto();
        processStartDto.setTenantId(2L);
        processStartDto.setProcessDefName(EVENT);
        processStartDto.setProcessName("张三申请发布文章");
        processStartDto.setCreator("1");
        processStartDto.setBusinessId("1");
        final Integer approveStatus = workflowInvoker.startProcessByName(processStartDto);
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(1));
    }

    @Test
    public void testExecuteTask(){
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(EVENT);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("1");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(2));
        }
    }

}
