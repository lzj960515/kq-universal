package com.kqinfo.universal.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kqinfo.universal.workflow.domain.HistoryProcessInstance;
import com.kqinfo.universal.workflow.domain.ProcessInstance;

/**
 * 历史流程实例 服务类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface HistoryProcessInstanceService extends IService<HistoryProcessInstance> {

	/**
	 * 创建历史流程
	 * @param processInstance 流程实例
	 */
	void create(ProcessInstance processInstance);

	/**
	 * 完成流程
	 * @param processInstanceId 流程实例id
	 * @param status 状态
	 */
	void complete(Long processInstanceId, Integer status);

}
