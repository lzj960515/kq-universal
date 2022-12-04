package com.kqinfo.universal.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.domain.HistoryTaskOperator;
import com.kqinfo.universal.workflow.domain.TaskOperator;
import com.kqinfo.universal.workflow.mapper.TaskOperatorMapper;
import com.kqinfo.universal.workflow.service.HistoryTaskOperatorService;
import com.kqinfo.universal.workflow.service.TaskOperatorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private HistoryTaskOperatorService historyTaskOperatorService;

    @Override
    public List<TaskOperator> listByTaskId(Long taskId) {
        LambdaQueryWrapper<TaskOperator> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TaskOperator::getTaskId, taskId);
        return super.list(wrapper);
    }

    @Override
    public void removeByTaskId(Long taskId) {
        // 先存入到历史表中
        List<TaskOperator> taskOperators = listByTaskId(taskId);
        for (TaskOperator taskOperator : taskOperators) {
            HistoryTaskOperator historyTaskOperator = new HistoryTaskOperator();
            historyTaskOperator.setTaskId(taskId);
            historyTaskOperator.setOperatorId(taskOperator.getOperatorId());
            historyTaskOperator.setOperatorName(taskOperator.getOperatorName());
            historyTaskOperatorService.save(historyTaskOperator);
        }
        LambdaQueryWrapper<TaskOperator> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TaskOperator::getTaskId, taskId);
        super.remove(wrapper);
    }

    @Override
    public void savePreTaskOperator(Long parentTaskId, Long taskId) {
        // 取出父节点的任务受理人
        List<HistoryTaskOperator> historyTaskOperators = historyTaskOperatorService.listByTaskId(parentTaskId);
        for (HistoryTaskOperator historyTaskOperator : historyTaskOperators) {
            // 重新保存
            TaskOperator taskOperator = new TaskOperator();
            taskOperator.setTaskId(taskId);
            taskOperator.setOperatorId(historyTaskOperator.getOperatorId());
            taskOperator.setOperatorName(historyTaskOperator.getOperatorName());
            super.save(taskOperator);
        }
    }
}
