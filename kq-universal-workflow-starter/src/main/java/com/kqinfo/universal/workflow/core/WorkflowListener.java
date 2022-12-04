package com.kqinfo.universal.workflow.core;

/**
 * @author Zijian Liao
 * @since 2.6.0
 */
public interface WorkflowListener {

    /**
     * 处理工作流事件
     *
     * @param event 事件
     */
    void onWorkflowEvent(WorkflowEvent event);

    /**
     * 处理工作流拒绝事件
     *
     * @param event 拒绝事件
     */
    default void onRejectEvent(WorkflowEvent event) {
    }
}
