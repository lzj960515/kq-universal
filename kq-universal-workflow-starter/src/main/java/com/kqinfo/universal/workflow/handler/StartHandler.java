package com.kqinfo.universal.workflow.handler;

import com.kqinfo.universal.workflow.core.Execution;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class StartHandler extends NodeHandler {

    @Override
    public void execute(WorkNode workNode, Execution execution) {
        // 找到下个节点, 直接执行
        final List<TransitionNode> transitionNodes = workNode.getTransitions();
        for (TransitionNode transitionNode : transitionNodes) {
            final WorkNode node = workflowHandler.getNode(transitionNode.getTo(), execution.getWorkNodes());
            workflowHandler.execute(node, execution);
        }
        // 开始流程时执行相应的事件
        workflowListenerExecutor.execute(workNode.getEvent(), execution.getProcessInstance().getBusinessId());
    }

}
