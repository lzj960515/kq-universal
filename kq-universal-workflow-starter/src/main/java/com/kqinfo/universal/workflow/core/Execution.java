package com.kqinfo.universal.workflow.core;

import com.kqinfo.universal.workflow.domain.Process;
import com.kqinfo.universal.workflow.domain.ProcessInstance;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.model.WorkNode;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
public class Execution {

	private WorkflowEngine workflowEngine;

	private Process process;

	private ProcessInstance processInstance;

	private Task task;

	private List<WorkNode> workNodes;

	private Map<String, Object> variables;

}
