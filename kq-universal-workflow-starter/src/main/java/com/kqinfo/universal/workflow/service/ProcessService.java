package com.kqinfo.universal.workflow.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.kqinfo.universal.workflow.domain.Process;

import java.util.List;

/**
 * 流程定义 服务类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface ProcessService extends IService<Process> {

	/**
	 * 通过名称获取流程定义
	 * @param name 流程定义名称
	 * @return 流程定义
	 */
	Process getProcessByName(String name);

	/**
	 * 通过名称获取流程定义列表
	 * @param name 流程定义名称
	 * @return 流程定义列表
	 */
	List<Process> listProcessByName(String name);

	/**
	 * 通过描述获取流程定义列表
	 * @param desc 流程定义名称
	 * @return 流程定义列表
	 */
	List<Process> listProcessByDesc(String desc);
}
