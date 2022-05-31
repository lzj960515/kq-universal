package com.kqinfo.universal.workflow.core;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kqinfo.universal.workflow.abs.WorkflowUserService;
import com.kqinfo.universal.workflow.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class UserAssignHandler {

	private final WorkflowUserService workflowUserService;

	public JSONObject getAssignee(String assignee) {
		final JSONObject jsonObject = new JSONObject();
		if (StringUtils.hasText(assignee)) {
			final String[] assignees = assignee.split(",");
			final List<String> userIds = Arrays.asList(assignees);
			final List<UserDto> users = workflowUserService.loadByUserIds(userIds);
			for (UserDto user : users) {
				jsonObject.set(user.getUserId(), user.getUsername());
			}
		}
		return jsonObject;
	}

}
