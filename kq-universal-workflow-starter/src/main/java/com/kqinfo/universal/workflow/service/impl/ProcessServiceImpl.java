package com.kqinfo.universal.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.domain.Process;
import com.kqinfo.universal.workflow.mapper.ProcessMapper;
import com.kqinfo.universal.workflow.service.ProcessService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程定义 服务实现类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Override
    public Process getProcessByName(String name) {
        LambdaQueryWrapper<Process> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Process::getName, name).orderByDesc(Process::getVersion).last("limit 1");
        return super.getOne(wrapper);
    }

    @Override
    public List<Process> listProcessByName(String name) {
        LambdaQueryWrapper<Process> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Process::getName, name);
        return super.list(wrapper);
    }

    @Override
    public List<Process> listProcessByDesc(String desc) {
        LambdaQueryWrapper<Process> wrapper = Wrappers.lambdaQuery();
        wrapper.like(Process::getDescription, desc);
        return super.list(wrapper);
    }
}
