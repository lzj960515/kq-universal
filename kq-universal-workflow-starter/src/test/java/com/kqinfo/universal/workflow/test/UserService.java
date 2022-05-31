package com.kqinfo.universal.workflow.test;

import com.kqinfo.universal.workflow.abs.WorkflowUserService;
import com.kqinfo.universal.workflow.dto.UserDto;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class UserService implements WorkflowUserService {

    @Override
    public UserDto loadByUserId(String userId) {
        return new UserDto(userId, "张三");
    }

    @Override
    public List<UserDto> loadByUserIds(List<String> userIds) {
        List<UserDto> userDtos = Lists.newArrayList();
        int i = 1;
        for (String userId : userIds) {
            userDtos.add(new UserDto(userId, "张三" + i));
            i ++ ;
        }
        return userDtos;
    }

    @Override
    public List<UserDto> loadByRole(Long tenantId, List<String> roles) {
        List<UserDto> userDtos = Lists.newArrayList();
        userDtos.add(new UserDto("1", "张三1"));
        userDtos.add(new UserDto("2", "张三2"));
        return userDtos;
    }
}
