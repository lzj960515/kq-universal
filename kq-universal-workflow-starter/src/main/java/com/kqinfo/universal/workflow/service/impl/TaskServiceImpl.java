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
 * ???????????? ???????????????
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
        // ?????????????????????
        // ???????????????????????????
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
        // ?????????????????????????????????????????????????????????
        LambdaQueryWrapper<Task> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Task::getBusinessId, businessId)
                .in(Task::getProcessId, processIds);
        return super.getOne(wrapper);
    }

    @Override
    public Task complete(String processName, String businessId, String operator, Integer status, String reason) {
        final Task task = getTask(businessId, processName);
        if(task == null){
            throw new WorkflowException("??????????????????????????????");
        }
        final List<TaskOperator> taskOperators = taskOperatorService.listByTaskId(task.getId());

        TaskOperator taskOperator;
        // ?????????????????????|???????????????????????????????????????????????????
        if (TaskStatusEnum.SUBMIT.value().equals(status) || TaskStatusEnum.CANCEL.value().equals(status)) {
            taskOperator = new TaskOperator();
            taskOperator.setOperatorId(operator);
            final UserDto user = workflowUserService.loadByUserId(operator);
            taskOperator.setOperatorName(user.getUsername());
        } else {
            // ???????????????????????????
            final Optional<TaskOperator> optional = taskOperators.stream()
                    .filter(item -> item.getOperatorId().equals(operator)).findFirst();
            if (!optional.isPresent()) {
                throw new WorkflowException("?????????????????????????????????");
            }
            taskOperator = optional.get();
        }

        // ????????????
        super.removeById(task.getId());
        // ???????????????????????????
        taskOperatorService.removeByTaskId(task.getId());
        // ??????????????????????????????
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
        // ??????????????????????????????
        final Process process = processService.getProcessByName(processName);
        final List<WorkNode> workNodes = workflowHandler.parse(process.getContext());
        final List<TaskNode> taskNodes = workflowHandler.getTaskNodes(workNodes);
        // ??????????????????????????????????????????
        final Task task = getTask(businessId, processName);
        // ???????????????????????????????????????
        final List<ApproveProgressDto> sortNodes = taskNodes.stream().sorted(Comparator.comparing(TaskNode::getSequence)).map(node -> new ApproveProgressDto().setPointName(node.getName())
                .setApproved(0)).collect(Collectors.toList());
        List<HistoryTask> historyTasks;
        if (task != null) {
            // ??????????????????????????????????????????id???????????????????????????
            historyTasks = historyTaskService.listByInstanceId(task.getInstanceId());
        } else {
            // ???????????????????????????????????????????????????????????????????????????????????????
            historyTasks = historyTaskService.getLastTasks(businessId, getProcessIds(processName));
        }

        return buildResultNodes(sortNodes, historyTasks, task);
    }

    private List<ApproveProgressDto> buildResultNodes(List<ApproveProgressDto> sortNodes, List<HistoryTask> historyTasks, Task task){
        // ???????????????????????????????????????
        final Map<String, HistoryTask> historyTaskMap = historyTasks.stream().collect(Collectors.toMap(HistoryTask::getName, Function.identity()));
        List<ApproveProgressDto> returnNodes = new ArrayList<>(sortNodes.size());
        for (ApproveProgressDto sortNode : sortNodes) {
            if(historyTaskMap.containsKey(sortNode.getPointName())){
                // ??????????????????????????????????????????????????????????????????????????????
                // ??????????????????????????????
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
        // ???????????????????????????????????????????????????????????????
        if(task != null){
            boolean startAdd = false;
            for (ApproveProgressDto sortNode : sortNodes) {
                if(startAdd){
                    returnNodes.add(sortNode);
                }
                // ?????????????????????????????????
                if(!startAdd && sortNode.getPointName().equals(task.getName())){
                    returnNodes.add(sortNode);
                    // ??????????????????????????????????????????
                    startAdd = true;
                }
            }
        }

        // ???????????????????????????
        ApproveProgressDto approvedNode = new ApproveProgressDto();
        approvedNode.setPointName("????????????");
        // ?????????????????????????????????????????????????????????
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
