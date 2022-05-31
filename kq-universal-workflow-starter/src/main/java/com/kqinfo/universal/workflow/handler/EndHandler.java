package com.kqinfo.universal.workflow.handler;

import com.kqinfo.universal.workflow.constant.StatusEnum;
import com.kqinfo.universal.workflow.core.Execution;
import com.kqinfo.universal.workflow.model.WorkNode;
import com.kqinfo.universal.workflow.service.ProcessInstanceService;
import org.springframework.stereotype.Component;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class EndHandler extends NodeHandler {

	@Override
	public void execute(WorkNode workNode, Execution execution) {
		final ProcessInstanceService processInstanceService = execution.getWorkflowEngine().processInstanceService();
		processInstanceService.complete(execution.getProcessInstance().getId(), StatusEnum.END.value());
		// 结束流程之后执行对应的事件
		workflowListenerExecutor.execute(workNode.getEvent(), execution.getProcessInstance().getBusinessId());
	}

}
