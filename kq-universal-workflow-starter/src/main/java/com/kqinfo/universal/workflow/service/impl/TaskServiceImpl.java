package com.kqinfo.universal.workflow.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.abs.WorkflowUserService;
import com.kqinfo.universal.workflow.constant.TaskStatusEnum;
import com.kqinfo.universal.workflow.constant.WorkflowConstant;
import com.kqinfo.universal.workflow.context.WorkflowContext;
import com.kqinfo.universal.workflow.core.AssignHandler;
import com.kqinfo.universal.workflow.core.WorkflowHandler;
import com.kqinfo.universal.workflow.domain.HistoryTask;
import com.kqinfo.universal.workflow.domain.Process;
import com.kqinfo.universal.workflow.domain.ProcessInstance;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.domain.TaskOperator;
import com.kqinfo.universal.workflow.dto.ApproveProgressDto;
import com.kqinfo.universal.workflow.dto.TaskLogDto;
import com.kqinfo.universal.workflow.dto.TodoTaskDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageParam;
import com.kqinfo.universal.workflow.dto.TodoTaskPageDto;
import com.kqinfo.universal.workflow.dto.UserDto;
import com.kqinfo.universal.workflow.exception.WorkflowException;
import com.kqinfo.universal.workflow.mapper.TaskMapper;
import com.kqinfo.universal.workflow.model.TaskNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import com.kqinfo.universal.workflow.service.HistoryTaskService;
import com.kqinfo.universal.workflow.service.ProcessService;
import com.kqinfo.universal.workflow.service.TaskOperatorService;
import com.kqinfo.universal.workflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流程任务 服务实现类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final TaskOperatorService taskOperatorService;

    private final HistoryTaskService historyTaskService;

    private final ProcessService processService;
    private final AssignHandler assignHandler;

    @Autowired
    private WorkflowUserService workflowUserService;

    @Autowired
    private WorkflowHandler workflowHandler;

    @Override
    public Task create(String name, Long parentId, Long processId, ProcessInstance processInstance,
                       JSONObject variables, TaskNode taskNode) {
        Task task = new Task();
        task.setName(name);
        task.setParentId(parentId);
        task.setProcessId(processId);
        task.setInstanceId(processInstance.getId());
        task.setBusinessId(processInstance.getBusinessId());
        task.setTenantId(WorkflowContext.getTenantId());
        String callUri = workflowHandler.parseCallUri(taskNode.getCallUri(), processInstance.getBusinessId());
        if(StringUtils.isEmpty(callUri)){
            callUri = processInstance.getCallUri();
        }
        task.setCallUri(callUri);
        super.save(task);
        // 保存任务关系人
        // 判断是否指定受理人
        JSONObject taskUserJson;
        if (variables.containsKey(WorkflowConstant.NEXT_TASK_ASSIGNEE)) {
            taskUserJson = variables.getJSONObject(WorkflowConstant.NEXT_TASK_ASSIGNEE);
        } else {
            taskUserJson =  assignHandler.getAssign(taskNode);
        }
        taskUserJson.forEach((userId, username) -> this.createTaskOperator(task.getId(), userId, username.toString()));
        return task;
    }

    private void createTaskOperator(Long taskId, String userId, String username) {
        TaskOperator taskOperator = new TaskOperator();
        taskOperator.setTaskId(taskId);
        taskOperator.setOperatorId(userId);
        taskOperator.setOperatorName(username);
        taskOperatorService.save(taskOperator);
    }

    @Override
    public Task getTask(String businessId, String processName) {
        final List<Long> processIds = getProcessIds(processName);
        // 一个流程对应一个业务单时只会有一个任务
        LambdaQueryWrapper<Task> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Task::getBusinessId, businessId)
                .in(Task::getProcessId, processIds);
        return super.getOne(wrapper);
    }

    @Override
    public Task complete(String processName, String businessId, String operator, Integer status, String reason) {
        final Task task = getTask(businessId, processName);
        if(task == null){
            throw new WorkflowException("未找到需要审核的任务");
        }
        final List<TaskOperator> taskOperators = taskOperatorService.listByTaskId(task.getId());

        TaskOperator taskOperator;
        // 如果是提交审核|取消审核，那么就直接使用任务发起人
        if (TaskStatusEnum.SUBMIT.value().equals(status) || TaskStatusEnum.CANCEL.value().equals(status)) {
            taskOperator = new TaskOperator();
            taskOperator.setOperatorId(operator);
            final UserDto user = workflowUserService.loadByUserId(operator);
            taskOperator.setOperatorName(user.getUsername());
        } else {
            // 判断受理人是否存在
            final Optional<TaskOperator> optional = taskOperators.stream()
                    .filter(item -> item.getOperatorId().equals(operator)).findFirst();
            if (!optional.isPresent()) {
                throw new WorkflowException("该受理人无权处理该任务");
            }
            taskOperator = optional.get();
        }

        // 删除任务
        super.removeById(task.getId());
        // 删除任务委托人关系
        taskOperatorService.removeByTaskId(task.getId());
        // 保存任务到历史任务表
        HistoryTask historyTask = new HistoryTask();
        BeanUtils.copyProperties(task, historyTask);

        historyTask.setOperatorId(taskOperator.getOperatorId());
        historyTask.setOperatorName(taskOperator.getOperatorName());
        historyTask.setStatus(status);
        historyTask.setReason(reason);
        historyTask.setTenantId(task.getTenantId());
        historyTask.setCreateTime(null);
        historyTask.setUpdateTime(null);
        historyTaskService.save(historyTask);

        return task;
    }

    @Override
    public List<TodoTaskDto> listTodoTask(String tenantId, String userId, String processName) {
        if (StringUtils.hasText(processName)) {
            final List<Long> processIds = getProcessIds(processName);
            return super.baseMapper.selectTodoTask(tenantId, userId, processIds);
        }
        return super.baseMapper.selectTodoTask(tenantId, userId, new ArrayList<>());
    }

    @Override
    public List<TodoTaskDto> listTodoTaskByDesc(String tenantId, String userId, String processDefDesc) {
        if(StringUtils.hasText(processDefDesc)){
            List<Long> processIds = getProcessIdsByDesc(processDefDesc);
            return super.baseMapper.selectTodoTask(tenantId, userId, processIds);
        }
        return super.baseMapper.selectTodoTask(tenantId, userId, new ArrayList<>());
    }

    @Override
    public IPage<TodoTaskPageDto> pageTodoTask(String tenantId, String userId, TodoTaskPageParam todoTaskPageParam) {
        final String processDefName = todoTaskPageParam.getProcessDefName();
        List<Long> processIds = new ArrayList<>();
        if (StringUtils.hasText(processDefName)) {
            processIds = getProcessIds(processDefName);
        }
        return super.baseMapper.pageTodoTask(new Page<>(todoTaskPageParam.getCurrent(), todoTaskPageParam.getSize()), tenantId, userId, processIds, todoTaskPageParam);
    }

    @Override
    public Integer hasTask(String userId, String processName, String businessId) {
        return super.baseMapper.hasTask(userId, businessId, getProcessIds(processName));
    }

    @Override
    public List<TaskLogDto> listTaskLog(String businessId, String processName) {
        return historyTaskService.listTaskLog(businessId, getProcessIds(processName));
    }

    @Override
    public List<ApproveProgressDto> approveProgress(String businessId, String processName) {
        // 查询出所有的任务节点
        final Process process = processService.getProcessByName(processName);
        final List<WorkNode> workNodes = workflowHandler.parse(process.getContext());
        final List<TaskNode> taskNodes = workflowHandler.getTaskNodes(workNodes);
        // 判断当前是否有正在执行的任务
        final Task task = getTask(businessId, processName);
        // 默认所有节点都是审核未完成
        final List<ApproveProgressDto> sortNodes = taskNodes.stream().sorted(Comparator.comparing(TaskNode::getSequence)).map(node -> new ApproveProgressDto().setPointName(node.getName())
                .setApproved(0)).collect(Collectors.toList());
        List<HistoryTask> historyTasks;
        if (task != null) {
            // 如果有，则使用当前的流程实例id查询历史完成的任务
            historyTasks = historyTaskService.listByInstanceId(task.getInstanceId());
        } else {
            // 如果没有，说明审核结束，未发起新一轮审核，查询上一轮的记录
            historyTasks = historyTaskService.getLastTasks(businessId, getProcessIds(processName));
        }

        return buildResultNodes(sortNodes, historyTasks, task);
    }

    private List<ApproveProgressDto> buildResultNodes(List<ApproveProgressDto> sortNodes, List<HistoryTask> historyTasks, Task task){
        // 先根据历史任务组装审核进度
        final Map<String, HistoryTask> historyTaskMap = historyTasks.stream().collect(Collectors.toMap(HistoryTask::getName, Function.identity()));
        List<ApproveProgressDto> returnNodes = new ArrayList<>(sortNodes.size());
        for (ApproveProgressDto sortNode : sortNodes) {
            if(historyTaskMap.containsKey(sortNode.getPointName())){
                // 如果历史任务完成了该节点，则将该节点添加到审核进度中
                // 判断任务是否审核通过
                final HistoryTask historyTask = historyTaskMap.get(sortNode.getPointName());
                if (TaskStatusEnum.REJECT.value().equals(historyTask.getStatus())
                        || TaskStatusEnum.CANCEL.value().equals(historyTask.getStatus())) {
                    sortNode.setApproved(0);
                } else {
                    sortNode.setApproved(1);
                }
                returnNodes.add(sortNode);
            }
        }
        // 当有执行任务时从流程定义中获取到后续的节点
        if(task != null){
            boolean startAdd = false;
            for (ApproveProgressDto sortNode : sortNodes) {
                if(startAdd){
                    returnNodes.add(sortNode);
                }
                // 定位到当前审核中的节点
                if(!startAdd && sortNode.getPointName().equals(task.getName())){
                    returnNodes.add(sortNode);
                    // 并把后续的所有节点加到进度中
                    startAdd = true;
                }
            }
        }

        // 补一个审核通过节点
        ApproveProgressDto approvedNode = new ApproveProgressDto();
        approvedNode.setPointName("审核通过");
        // 如果最后一个节点是审核通过，则审核通过
        final ApproveProgressDto lastNode = returnNodes.get(returnNodes.size() - 1);
        approvedNode.setApproved(lastNode.getApproved());
        returnNodes.add(approvedNode);
        return returnNodes;
    }

    private List<Long> getProcessIds(String processDefName) {
        final List<Process> processes = processService.listProcessByName(processDefName);
        return processes.stream().map(Process::getId).collect(Collectors.toList());
    }

    private List<Long> getProcessIdsByDesc(String processDefDesc) {
        final List<Process> processes = processService.listProcessByDesc(processDefDesc);
        return processes.stream().map(Process::getId).collect(Collectors.toList());
    }
}
