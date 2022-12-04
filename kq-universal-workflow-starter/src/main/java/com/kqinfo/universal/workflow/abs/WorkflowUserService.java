package com.kqinfo.universal.workflow.abs;

import com.kqinfo.universal.workflow.dto.UserDto;

import java.util.Collections;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface WorkflowUserService {

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserDto loadByUserId(String userId);

    /**
     * 获取用户信息列表
     *
     * @param userIds 用户id列表
     * @return 用户信息列表
     */
    default List<UserDto> loadByUserIds(List<String> userIds) {
        return Collections.emptyList();
    }

    /**
     * 使用角色获取用户信息列表
     *
     * @param tenantId 租户id
     * @param roles    角色列表
     * @return 用户信息
     */
    default List<UserDto> loadByRole(Long tenantId, List<String> roles) {
        return Collections.emptyList();
    }
}
