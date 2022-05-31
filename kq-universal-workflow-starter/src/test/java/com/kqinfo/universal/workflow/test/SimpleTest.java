package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.constant.StatusEnum;
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
@SpringBootTest
public class SimpleTest {

    private static final String TEST = "测试流程";
    @Resource
    private WorkflowInvoker workflowInvoker;

    @Test
    public void testStart(){
        ProcessStartDto processStartDto = new ProcessStartDto();
        processStartDto.setTenantId(2L);
        processStartDto.setProcessDefName(TEST);
        processStartDto.setProcessName("张三申请发布文章");
        processStartDto.setCreator("1");
        processStartDto.setBusinessId("1");
        final Integer approveStatus = workflowInvoker.startProcessByName(processStartDto);
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
    }

    @Test
    public void testExecuteTask(){
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(TEST);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("1");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
        }
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(TEST);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("2");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.END.value()));
        }
    }

    @Test
    public void testRejectTask(){
        final Integer approveStatus = workflowInvoker.rejectTask(TEST, "1", "1", "测试拒绝");
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.REJECT.value()));
    }

    @Test
    public void testStartAndExecuteFirstTask(){
        ProcessStartDto processStartDto = new ProcessStartDto();
        processStartDto.setTenantId(2L);
        processStartDto.setProcessDefName(TEST);
        processStartDto.setProcessName("张三申请发布文章");
        processStartDto.setCreator("1");
        processStartDto.setBusinessId("1");
        final Integer approveStatus = workflowInvoker.startProcessAndExecuteFirstTask(processStartDto);
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
    }

    /**
     * 启动流程并指定下个任务的受理人
     */
    @Test
    public void testStartAndAssignUser(){
        {
            ProcessStartDto processStartDto = new ProcessStartDto();
            processStartDto.setTenantId(2L);
            processStartDto.setProcessDefName(TEST);
            processStartDto.setProcessName("张三申请发布文章");
            processStartDto.setCreator("1");
            processStartDto.setBusinessId("1");
            processStartDto.setNextTaskAssigneeList(Collections.singletonList(new Assignee("3", "李四")));
            final Integer approveStatus = workflowInvoker.startProcessByName(processStartDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
        }
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(TEST);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("3");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.START.value()));
        }
        {
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(TEST);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("2");
            executeTaskDto.setTenantId(2L);
            final Integer approveStatus = workflowInvoker.executeTask(executeTaskDto);
            MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.END.value()));
        }
    }

    /**
     * 测试非任务受理人去审核任务
     */
    @Test
    public void testExecuteFail(){
        Assertions.assertThrows(WorkflowException.class, ()->{
            ExecuteTaskDto executeTaskDto = new ExecuteTaskDto();
            executeTaskDto.setProcessDefName(TEST);
            executeTaskDto.setBusinessId("1");
            executeTaskDto.setOperator("1");
            executeTaskDto.setTenantId(2L);
            workflowInvoker.executeTask(executeTaskDto);
        });
    }

    @Test
    public void testCancelProcess(){
        final Integer approveStatus = workflowInvoker.cancelProcess(TEST, "1", "1");
        MatcherAssert.assertThat(approveStatus, CoreMatchers.is(StatusEnum.CANCEL.value()));
    }
}
