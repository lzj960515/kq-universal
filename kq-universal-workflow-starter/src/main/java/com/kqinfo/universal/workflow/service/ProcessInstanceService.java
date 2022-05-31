package com.kqinfo.universal.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kqinfo.universal.workflow.domain.ProcessInstance;

/**
 * 流程实例 服务类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface ProcessInstanceService extends IService<ProcessInstance> {

	/**
	 * 创建流程实例
	 * @param name 流程名称
	 * @param callUri 回调地址
	 * @param processId 流程id
	 * @param businessId 业务id
	 * @param creator 发起人
	 * @param variable 流程变量
	 * @return {@link ProcessInstance}
	 */
	ProcessInstance create(String name, String callUri, Long processId, String businessId, String creator,
			String variable);

	/**
	 * 完成流程
	 * @param processInstanceId 流程实例id
	 * @param status 状态
	 */
	void complete(Long processInstanceId, Integer status);

}
