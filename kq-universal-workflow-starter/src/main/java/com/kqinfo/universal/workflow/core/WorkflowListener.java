package com.kqinfo.universal.workflow.core;

/**
 * @author Zijian Liao
 * @since 2.6.0
 */
public interface WorkflowListener {

    /**
     * 处理工作流事件
     * @param event 事件
     */
    void onWorkflowEvent(WorkflowEvent event);
}
