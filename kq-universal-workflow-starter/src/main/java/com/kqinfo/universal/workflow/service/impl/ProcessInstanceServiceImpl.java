package com.kqinfo.universal.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.workflow.domain.ProcessInstance;
import com.kqinfo.universal.workflow.mapper.ProcessInstanceMapper;
import com.kqinfo.universal.workflow.service.HistoryProcessInstanceService;
import com.kqinfo.universal.workflow.service.ProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 流程实例 服务实现类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProcessInstanceServiceImpl extends ServiceImpl<ProcessInstanceMapper, ProcessInstance>
        implements ProcessInstanceService {

    private final HistoryProcessInstanceService historyProcessInstanceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProcessInstance create(String name, String callUri, Long processId, String businessId, String creator,
                                  String variable) {
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setName(name);
        processInstance.setCallUri(callUri);
        processInstance.setProcessId(processId);
        processInstance.setBusinessId(businessId);
        processInstance.setCreator(creator);
        processInstance.setVariable(variable);
        super.save(processInstance);
        historyProcessInstanceService.create(processInstance);
        return processInstance;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void complete(Long processInstanceId, Integer status) {
        // 删除流程实例
        super.removeById(processInstanceId);
        // 将历史流程实例置为已完成
        historyProcessInstanceService.complete(processInstanceId, status);
    }

}
