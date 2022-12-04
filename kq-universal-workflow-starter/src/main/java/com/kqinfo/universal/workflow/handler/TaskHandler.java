package com.kqinfo.universal.workflow.handler;

import cn.hutool.json.JSONUtil;
import com.kqinfo.universal.workflow.core.Execution;
import com.kqinfo.universal.workflow.domain.ProcessInstance;
import com.kqinfo.universal.workflow.model.TaskNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import com.kqinfo.universal.workflow.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class TaskHandler extends NodeHandler {

    @Resource
    private TaskService taskService;

    @Override
    public void execute(WorkNode workNode, Execution execution) {
        TaskNode taskNode = (TaskNode) workNode;
        final ProcessInstance processInstance = execution.getProcessInstance();
        // 保存任务
        taskService.create(taskNode.getName(), execution.getTask() != null ? execution.getTask().getId() : 0L,
                execution.getProcess().getId(),
                processInstance,
                JSONUtil.parseObj(execution.getVariables()),
                taskNode);

    }

}
