package com.kqinfo.universal.workflow.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kqinfo.universal.workflow.context.WorkflowContext;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.dto.ApproveProgressDto;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessDefConfig;
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
import java.util.function.Supplier;

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
    public void initProcess(ProcessDefConfig processDefConfig) {
        Assert.hasText(processDefConfig.getName(), "流程名称不能为空");
        Assert.hasText(processDefConfig.getDesc(), "流程定义不能为空");
        workflowEngine.initProcess(processDefConfig);
    }

    @Override
    public Integer startProcessByName(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return this.executeFunc(processStartDto.getCreator(), () -> workflowEngine.startProcessByName(processStartDto, true));
    }

    @Override
    public Integer startProcessByNameNoCancel(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return this.executeFunc(processStartDto.getCreator(), () -> workflowEngine.startProcessByName(processStartDto, false));
    }

    @Override
    public Integer startProcessAndExecuteFirstTask(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return this.executeFunc(processStartDto.getCreator(), () -> workflowEngine.startProcessAndExecuteFirstTask(processStartDto, true));
    }

    @Override
    public Integer startProcessAndExecuteFirstTaskNoCancel(ProcessStartDto processStartDto) {
        checkStartParam(processStartDto);
        return this.executeFunc(processStartDto.getCreator(), () -> workflowEngine.startProcessAndExecuteFirstTask(processStartDto, false));
    }

    @Override
    public Integer cancelProcess(String processDefName, String businessId, String operator) {
        return workflowEngine.cancelProcess(processDefName, businessId, operator);
    }

    private void checkStartParam(ProcessStartDto processStartDto) {
        Assert.notNull(processStartDto.getTenantId(), "租户id不能为空");
        Assert.notNull(processStartDto.getProcessDefName(), "流程定义不能为空");
        Assert.notNull(processStartDto.getProcessName(), "流程名称不能为空");
        Assert.notNull(processStartDto.getBusinessId(), "业务id不能为空");
        Assert.notNull(processStartDto.getCreator(), "流程发起人不能为空");
    }

    @Override
    public Integer executeTask(ExecuteTaskDto executeTaskDto) {
        Assert.notNull(executeTaskDto.getTenantId(), "租户id不能为空");
        Assert.notNull(executeTaskDto.getProcessDefName(), "流程定义不能为空");
        Assert.notNull(executeTaskDto.getBusinessId(), "业务id不能为空");
        Assert.notNull(executeTaskDto.getOperator(), "审核人不能为空");
        return this.executeFunc(executeTaskDto.getOperator(), () -> workflowEngine.executeTask(executeTaskDto));
    }

    @Override
    public Integer rejectTask(String processDefName, String businessId, String operator, String reason) {
        Assert.notNull(processDefName, "流程定义不能为空");
        Assert.notNull(businessId, "业务id不能为空");
        Assert.notNull(operator, "审核人不能为空");
        Assert.notNull(reason, "驳回原因不能为空");
        return this.executeFunc(operator, () -> workflowEngine.rejectTask(processDefName, businessId, operator, reason));
    }

    @Override
    public Integer rejectToPreNode(String processDefName, String businessId, String operator, String reason) {
        Assert.notNull(processDefName, "流程定义不能为空");
        Assert.notNull(businessId, "业务id不能为空");
        Assert.notNull(operator, "审核人不能为空");
        Assert.notNull(reason, "驳回原因不能为空");
        return this.executeFunc(operator, () -> workflowEngine.rejectToPreNode(processDefName, businessId, operator, reason));
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

    private Integer executeFunc(String operator, Supplier<Integer> func) {
        WorkflowContext.setOperator(operator);
        try {
            return func.get();
        } finally {
            WorkflowContext.removeOperator();
        }
    }
}
