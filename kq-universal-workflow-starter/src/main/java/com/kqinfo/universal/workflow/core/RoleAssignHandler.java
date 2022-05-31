package com.kqinfo.universal.workflow.core;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kqinfo.universal.workflow.abs.WorkflowUserService;
import com.kqinfo.universal.workflow.context.WorkflowContext;
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
public class RoleAssignHandler {

	private final WorkflowUserService workflowUserService;

	public JSONObject getAssignee(String roleStr) {
		final JSONObject jsonObject = new JSONObject();
		final Long tenantId = WorkflowContext.getTenantId();
		if (StringUtils.hasText(roleStr)) {
			final String[] roles = roleStr.split(",");
			final List<UserDto> users = workflowUserService.loadByRole(tenantId, Arrays.asList(roles));
			for (UserDto user : users) {
				jsonObject.set(user.getUserId(), user.getUsername());
			}
		}
		return jsonObject;
	}

}
