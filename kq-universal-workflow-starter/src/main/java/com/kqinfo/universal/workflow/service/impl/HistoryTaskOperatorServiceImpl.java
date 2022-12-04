package com.kqinfo.universal.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.domain.HistoryTaskOperator;
import com.kqinfo.universal.workflow.mapper.HistoryTaskOperatorMapper;
import com.kqinfo.universal.workflow.service.HistoryTaskOperatorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务处理人关系 服务实现类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class HistoryTaskOperatorServiceImpl extends ServiceImpl<HistoryTaskOperatorMapper, HistoryTaskOperator>
        implements HistoryTaskOperatorService {

    @Override
    public List<HistoryTaskOperator> listByTaskId(Long taskId) {
        LambdaQueryWrapper<HistoryTaskOperator> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HistoryTaskOperator::getTaskId, taskId);
        return super.list(wrapper);
    }
}
