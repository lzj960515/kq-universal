package com.kqinfo.universal.workflow.core;

import com.kqinfo.universal.workflow.exception.WorkflowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 时间执行器
 *
 * @author Zijian Liao
 * @since 2.6.0
 */
@Component
public class WorkflowListenerExecutor {

    @Autowired(required = false)
    private Map<String, WorkflowListener> listenerMap;

    public void execute(String event, String businessId){
        if(StringUtils.isEmpty(event)){
            return;
        }
        if(listenerMap == null){
            throw new WorkflowException("未找到事件["+event+"]对应的监听器");
        }
        final WorkflowListener workflowListener = listenerMap.get(event);
        if(workflowListener == null){
            throw new WorkflowException("未找到事件["+event+"]对应的监听器");
        }
        WorkflowEvent workflowEvent = new WorkflowEvent();
        workflowEvent.setBusinessId(businessId);
        workflowListener.onWorkflowEvent(workflowEvent);
    }

}
