package com.kqinfo.universal.workflow.core;

import cn.hutool.json.JSONObject;
import com.kqinfo.universal.workflow.model.TaskNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class AssignHandler {

	private final UserAssignHandler userAssignHandler;

	private final RoleAssignHandler roleAssignHandler;

	public JSONObject getAssign(TaskNode taskNode) {
		final String assignee = taskNode.getAssignee();
		final String role = taskNode.getRole();

		JSONObject assignJson = new JSONObject(16, false);
		assignJson.putAll(userAssignHandler.getAssignee(assignee));
		assignJson.putAll(roleAssignHandler.getAssignee(role));
		return assignJson;
	}

}
