package com.kqinfo.universal.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.dto.TodoTaskDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageParam;
import com.kqinfo.universal.workflow.dto.TodoTaskPageDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程任务 Mapper 接口
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface TaskMapper extends BaseMapper<Task> {


    /**
     * 查询待办任务列表
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processIds 流程定义id列表
     * @return 待办任务列表
     */
    List<TodoTaskDto> selectTodoTask(@Param("tenantId") String tenantId, @Param("userId") String userId, @Param("processIds") List<Long> processIds);

    /**
     * 分页查询待办任务列表
     * @param page 分页参数
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processIds 流程定义id列表
     * @param todoTaskPageParam 分页参数
     * @return 待办任务列表
     */
    IPage<TodoTaskPageDto> pageTodoTask(Page<TodoTaskPageDto> page, @Param("tenantId") String tenantId, @Param("userId") String userId, @Param("processIds") List<Long> processIds, @Param("param") TodoTaskPageParam todoTaskPageParam);

    /**
     * 查询该用户是否有任务
     * @param userId 用户id
     * @param businessId 业务id
     * @param processIds 流程定义id列表
     * @return 是否有任务
     */
    Integer hasTask(@Param("userId") String userId, @Param("businessId") String businessId, @Param("processIds") List<Long> processIds);
}
