package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.constant.StatusEnum;
import com.kqinfo.universal.workflow.core.WorkflowInvoker;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
    public void testStart() {
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
    public void testExecuteTask() {
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(EVENT);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("1");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
        }
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(EVENT);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("1");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.END.value()));
        }
    }

    @Test
    public void testRejectTask() {
        final Integer approveStatus = workflowInvoker.rejectTask(EVENT, "1", "1", "测试拒绝");
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.REJECT.value()));
    }

    @Test
    public void testRejectToPreNode() {
        final Integer approveStatus = workflowInvoker.rejectToPreNode(EVENT, "1", "1", "测试拒绝");
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
    }

}
