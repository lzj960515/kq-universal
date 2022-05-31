package com.kqinfo.universal.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.domain.TaskOperator;
import com.kqinfo.universal.workflow.mapper.TaskOperatorMapper;
import com.kqinfo.universal.workflow.service.TaskOperatorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务处理人关系 服务实现类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class TaskOperatorServiceImpl extends ServiceImpl<TaskOperatorMapper, TaskOperator>
		implements TaskOperatorService {

	@Override
	public List<TaskOperator> listByTaskId(Long taskId) {
		LambdaQueryWrapper<TaskOperator> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(TaskOperator::getTaskId, taskId);
		return super.list(wrapper);
	}

	@Override
	public void removeByTaskId(Long taskId) {
		LambdaQueryWrapper<TaskOperator> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(TaskOperator::getTaskId, taskId);
		super.remove(wrapper);
	}
}
