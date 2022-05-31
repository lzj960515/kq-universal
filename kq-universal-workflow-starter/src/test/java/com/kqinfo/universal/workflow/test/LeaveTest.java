package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.core.WorkflowInvoker;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import org.assertj.core.util.Maps;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 请假流程测试，支持分叉
 * @author Zijian Liao
 * @since 2.4.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LeaveTest {

    private static final String LEAVE = "请假审批";

    @Resource
    private WorkflowInvoker workflowInvoker;

    @Test
    public void testStart(){
        ProcessStartDto processStartDto = new ProcessStartDto();
        processStartDto.setTenantId(2L);
        processStartDto.setProcessDefName(LEAVE);
        processStartDto.setProcessName("张三申请请假");
        processStartDto.setCreator("1");
        processStartDto.setBusinessId("2");
        processStartDto.setVariables(Maps.newHashMap("day", 2));
        final Integer approveStatus = workflowInvoker.startProcessByName(processStartDto);
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(1));
    }

    /**
     * 大于等于2天流程到部门审批
     */
    @Test
    public void testExecuteTask(){
        ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
        executeTaskDto.setProcessDefName(LEAVE);
        executeTaskDto.setBusinessId("2");
        executeTaskDto.setOperator("1");
        executeTaskDto.setTenantId(2L);
        executeTaskDto.setReason("准予请假");
        final Integer approveStatus =  workflowInvoker.executeTask(executeTaskDto);
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(2));
    }

}
