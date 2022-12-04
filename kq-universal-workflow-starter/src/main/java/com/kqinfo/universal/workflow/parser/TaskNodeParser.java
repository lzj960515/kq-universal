package com.kqinfo.universal.workflow.parser;

import cn.hutool.json.JSONObject;
import com.kqinfo.universal.workflow.model.TaskNode;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class TaskNodeParser extends NodeParser {

    @Override
    protected WorkNode newNode() {
        return new TaskNode();
    }

    @Override
    protected void parse(JSONObject nodeJson, WorkNode workNode) {
        TaskNode taskNode = (TaskNode) workNode;
        taskNode.setAssignee(nodeJson.getStr("assignee")).setRole(nodeJson.getStr("role"));
        taskNode.setSequence(nodeJson.getInt("sequence"));
        taskNode.setCallUri(nodeJson.getStr("callUri"));
        final List<TransitionNode> transitionNodes = parseTransitionNode(nodeJson);
        taskNode.setTransitions(transitionNodes);
    }

}
