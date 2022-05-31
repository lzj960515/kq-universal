package com.kqinfo.universal.workflow.core;

import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import com.kqinfo.universal.workflow.service.ProcessInstanceService;
import io.swagger.models.auth.In;

import java.util.Map;

/**
 * 流程引擎
 *
 * @author Zijian Liao
 * @since 1.0.0.
 */
public interface WorkflowEngine {

	/**
	 * 初始化流程定义
	 */
	void initProcess();

	/**
	 * 通过流程定义名称启动流程
	 * @param processStartDto 流程启动参数
	 * @return 流程状态 1.审核中 2.审核通过 3.驳回
	 */
	Integer startProcessByName(ProcessStartDto processStartDto, boolean cancel);

	/**
	 * 通过流程定义名称启动流程, 并执行第一个任务
	 * @param processStartDto 流程启动参数
	 * @return 流程状态 1.审核中 2.审核通过 3.驳回
	 */
	Integer startProcessAndExecuteFirstTask(ProcessStartDto processStartDto, boolean cancel);

	/**
	 * 执行任务
	 * @param executeTaskDto 执行任务参数
	 * @return 流程状态 1.审核中 2.审核通过 3.驳回
	 */
	Integer executeTask(ExecuteTaskDto executeTaskDto);

	/**
	 * 驳回任务
	 * @param processDefName 流程定义名称
	 * @param businessId 业务id
	 * @param operator 任务受理人
	 * @param reason 原因
	 * @return 流程状态 1.审核中 2.审核通过 3.驳回
	 */
	Integer rejectTask(String processDefName, String businessId, String operator, String reason);

	/**
	 * 取消审核
	 * @param processDefName 流程定义名称
	 * @param businessId 业务id
	 * @param operator 取消人
	 * @return 流程状态 4.取消审核
	 */
	Integer cancelProcess(String processDefName, String businessId, String operator);

	/**
	 * 获取流程实例service
	 * @return 流程实例service
	 */
	ProcessInstanceService processInstanceService();

}
