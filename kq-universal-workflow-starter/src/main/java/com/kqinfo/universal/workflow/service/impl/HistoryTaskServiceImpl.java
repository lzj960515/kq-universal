package com.kqinfo.universal.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.domain.HistoryTask;
import com.kqinfo.universal.workflow.dto.TaskLogDto;
import com.kqinfo.universal.workflow.exception.WorkflowException;
import com.kqinfo.universal.workflow.mapper.HistoryTaskMapper;
import com.kqinfo.universal.workflow.service.HistoryTaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史流程任务 服务实现类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class HistoryTaskServiceImpl extends ServiceImpl<HistoryTaskMapper, HistoryTask>
		implements HistoryTaskService {

	@Override
	public List<TaskLogDto> listTaskLog(String businessId, List<Long> processIds) {
		LambdaQueryWrapper<HistoryTask> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HistoryTask::getBusinessId, businessId)
				.in(HistoryTask::getProcessId, processIds)
				.orderByDesc(HistoryTask::getCreateTime);
		final List<HistoryTask> list = super.list(wrapper);
		return list.stream().map(task -> new TaskLogDto()
				.setOperatorId(task.getOperatorId())
				.setOperator(task.getOperatorName())
				.setReason(task.getReason())
				.setTaskName(task.getName())
				.setStatus(task.getStatus())
				.setCreateTime(task.getCreateTime())).collect(Collectors.toList());
	}

	@Override
	public List<HistoryTask> listByInstanceId(Long instanceId) {
		LambdaQueryWrapper<HistoryTask> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HistoryTask::getInstanceId, instanceId);
		return super.list(wrapper);
	}

	@Override
	public List<HistoryTask> getLastTasks(String businessId, List<Long> processIds) {
		LambdaQueryWrapper<HistoryTask> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HistoryTask::getBusinessId, businessId)
				.in(HistoryTask::getProcessId, processIds)
				.orderByDesc(HistoryTask::getCreateTime);
		final List<HistoryTask> list = super.list(wrapper);
		if (list.isEmpty()) {
			throw new WorkflowException("该业务数据还未被审核过，无法展示审核进度，请先编辑资料审核后再试");
		}
		final HistoryTask historyTask = list.get(0);
		return listByInstanceId(historyTask.getInstanceId());
	}
}
