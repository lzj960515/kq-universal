package com.kqinfo.universal.workflow.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.dto.ApproveProgressDto;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import com.kqinfo.universal.workflow.dto.TaskLogDto;
import com.kqinfo.universal.workflow.dto.TodoTaskDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageParam;
import com.kqinfo.universal.workflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class WorkflowInvokerImpl implements WorkflowInvoker {

    private final WorkflowEngine workflowEngine;
    private final TaskService taskService;

    @Override
    public Integer startProcessByName(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return workflowEngine.startProcessByName(processStartDto, true);
    }

    @Override
    public Integer startProcessByNameNoCancel(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return workflowEngine.startProcessByName(processStartDto, false);
    }

    @Override
    public Integer startProcessAndExecuteFirstTask(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return workflowEngine.startProcessAndExecuteFirstTask(processStartDto, true);
    }

    @Override
    public Integer startProcessAndExecuteFirstTaskNoCancel(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return workflowEngine.startProcessAndExecuteFirstTask(processStartDto, false);
    }

    @Override
    public Integer cancelProcess(String processDefName, String businessId, String operator) {
        return workflowEngine.cancelProcess(processDefName, businessId, operator);
    }

    private void checkStartParam(ProcessStartDto processStartDto){
        Assert.notNull(processStartDto.getTenantId(), "??????id????????????");
        Assert.notNull(processStartDto.getProcessDefName(), "????????????????????????");
        Assert.notNull(processStartDto.getProcessName(), "????????????????????????");
        Assert.notNull(processStartDto.getBusinessId(), "??????id????????????");
        Assert.notNull(processStartDto.getCreator(), "???????????????????????????");
    }

    @Override
    public Integer executeTask(ExecuteTaskDto executeTaskDto) {
        Assert.notNull(executeTaskDto.getTenantId(), "??????id????????????");
        Assert.notNull(executeTaskDto.getProcessDefName(), "????????????????????????");
        Assert.notNull(executeTaskDto.getBusinessId(), "??????id????????????");
        Assert.notNull(executeTaskDto.getOperator(), "?????????????????????");
        return workflowEngine.executeTask(executeTaskDto);
    }

    @Override
    public Integer rejectTask(String processDefName, String businessId, String operator, String reason) {
        Assert.notNull(processDefName, "????????????????????????");
        Assert.notNull(businessId, "??????id????????????");
        Assert.notNull(operator, "?????????????????????");
        Assert.notNull(reason, "????????????????????????");
        return workflowEngine.rejectTask(processDefName, businessId, operator, reason);
    }

    @Override
    public List<TodoTaskDto> listTodoTask(String tenantId, String userId, String processDefName) {
        return taskService.listTodoTask(tenantId, userId, processDefName);
    }

    @Override
    public List<TodoTaskDto> listTodoTaskByDesc(String tenantId, String userId, String processDefDesc) {
        return taskService.listTodoTaskByDesc(tenantId, userId, processDefDesc);
    }

    @Override
    public IPage<TodoTaskPageDto> pageTodoTask(String tenantId, String userId, TodoTaskPageParam todoTaskPageParam) {
        return taskService.pageTodoTask(tenantId, userId, todoTaskPageParam);
    }

    @Override
    public Integer hasTask(String userId, String processDefName, String businessId) {
        return taskService.hasTask(userId, processDefName, businessId);
    }

    @Override
    public List<TaskLogDto> listTaskLog(String businessId, String processDefName) {
        return taskService.listTaskLog(businessId, processDefName);
    }

    @Override
    public List<ApproveProgressDto> approveProgress(String businessId, String processDefName) {
        return taskService.approveProgress(businessId, processDefName);
    }

    @Override
    public Task getTask(String businessId, String processDefName) {
        return taskService.getTask(businessId, processDefName);
    }
}
