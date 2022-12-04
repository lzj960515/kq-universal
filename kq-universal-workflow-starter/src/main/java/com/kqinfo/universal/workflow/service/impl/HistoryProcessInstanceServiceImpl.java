package com.kqinfo.universal.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.constant.StatusEnum;
import com.kqinfo.universal.workflow.domain.HistoryProcessInstance;
import com.kqinfo.universal.workflow.domain.ProcessInstance;
import com.kqinfo.universal.workflow.mapper.HistoryProcessInstanceMapper;
import com.kqinfo.universal.workflow.service.HistoryProcessInstanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 历史流程实例 服务实现类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
public class HistoryProcessInstanceServiceImpl extends
        ServiceImpl<HistoryProcessInstanceMapper, HistoryProcessInstance> implements HistoryProcessInstanceService {

    @Override
    public void create(ProcessInstance processInstance) {
        HistoryProcessInstance historyProcessInstance = new HistoryProcessInstance();
        BeanUtils.copyProperties(processInstance, historyProcessInstance);
        historyProcessInstance.setStatus(StatusEnum.START.value());
        super.save(historyProcessInstance);
    }

    @Override
    public void complete(Long processInstanceId, Integer status) {
        HistoryProcessInstance historyProcessInstance = new HistoryProcessInstance();
        historyProcessInstance.setId(processInstanceId);
        historyProcessInstance.setStatus(status);
        super.updateById(historyProcessInstance);
    }

}
