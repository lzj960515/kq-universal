package com.kqinfo.universal.workflow.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kqinfo.universal.workflow.domain.ProcessInstance;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.dto.TodoTaskDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageParam;
import com.kqinfo.universal.workflow.dto.ApproveProgressDto;
import com.kqinfo.universal.workflow.dto.TaskLogDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageDto;
import com.kqinfo.universal.workflow.model.TaskNode;

import java.util.List;

/**
 * 流程任务 服务类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface TaskService extends IService<Task> {

	/**
	 * 创建任务
	 * @param name 任务名称
	 * @param parentId 父级id
	 * @param processId 流程定义id
	 * @param processInstance 流程实例
	 * @param variables 流程变量
	 * @param taskNode 任务节点
	 * @return 任务
	 */
	Task create(String name, Long parentId, Long processId, ProcessInstance processInstance, JSONObject variables, TaskNode taskNode);

	/**
	 * 查询任务
	 * @param businessId 业务id
	 * @param processDefName 流程定义
	 * @return 任务
	 */
	Task getTask(String businessId, String processDefName);

	/**
	 * 完成任务
	 * @param processDefName 流程定义
	 * @param businessId 业务id
	 * @param operator 任务受理人
	 * @param status 状态
	 * @param reason 原因
	 * @return 当前任务
	 */
	Task complete(String processDefName, String businessId, String operator, Integer status, String reason);

	/**
	 * 查询待办任务列表
	 * @param tenantId 租户id
	 * @param userId 用户id
	 * @param processDefName 流程名称
	 * @return 办任务列表
	 */
	List<TodoTaskDto> listTodoTask(String tenantId, String userId, String processDefName);

	/**
	 * 查询10条待办任务，对标首页展示需求
	 * @param tenantId 租户id
	 * @param userId 用户id
	 * @param processDefDesc 流程描述
	 * @return 办任务列表
	 */
	List<TodoTaskDto> listTodoTaskByDesc(String tenantId, String userId, String processDefDesc);

	/**
	 * 分页查询待办任务列表
	 * @param tenantId 租户id
	 * @param userId 用户id
	 * @param todoTaskPageParam 参数
	 * @return 待办任务列表
	 */
	IPage<TodoTaskPageDto> pageTodoTask(String tenantId, String userId, TodoTaskPageParam todoTaskPageParam);

	/**
	 * 是否有任务
	 * @param userId 用户id
	 * @param processDefName 流程名称
	 * @param businessId 业务id
	 * @return 是否有任务 1.是 0.否
	 */
	Integer hasTask(String userId, String processDefName, String businessId);

	/**
	 * 查询审核日志
	 * @param businessId 业务id
	 * @param processDefName 流程定义名称
	 * @return 审核日志
	 */
	List<TaskLogDto> listTaskLog(String businessId, String processDefName);

	/**
	 * 查询审核进度
	 * @param businessId 业务id
	 * @param processDefName 流程定义名称
	 * @return 审核进度
	 */
	List<ApproveProgressDto> approveProgress(String businessId, String processDefName);
}
