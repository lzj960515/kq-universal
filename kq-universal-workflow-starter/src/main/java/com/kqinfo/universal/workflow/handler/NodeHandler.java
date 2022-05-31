package com.kqinfo.universal.workflow.handler;

import com.kqinfo.universal.workflow.core.Execution;
import com.kqinfo.universal.workflow.core.WorkflowHandler;
import com.kqinfo.universal.workflow.core.WorkflowListenerExecutor;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class NodeHandler {

	@Autowired
	protected WorkflowHandler workflowHandler;
	@Autowired
	protected WorkflowListenerExecutor workflowListenerExecutor;

	/**
	 * 执行节点任务
	 * @param workNode 节点
	 * @param execution 执行对象
	 */
	public abstract void execute(WorkNode workNode, Execution execution);

}
