package com.kqinfo.universal.workflow.parser;

import cn.hutool.json.JSONObject;
import com.kqinfo.universal.workflow.model.DecisionNode;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class DecisionNodeParser extends NodeParser {

    @Override
    protected WorkNode newNode() {
        return new DecisionNode();
    }

    @Override
    protected void parse(JSONObject nodeJson, WorkNode workNode) {
        DecisionNode decisionNode = (DecisionNode) workNode;
        decisionNode.setExpression(nodeJson.getStr("expression"));
        final List<TransitionNode> transitionNodes = parseTransitionNode(nodeJson, true);
        decisionNode.setTransitions(transitionNodes);
    }

}
