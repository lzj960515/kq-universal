package com.kqinfo.universal.workflow.parser;

import cn.hutool.json.JSONObject;
import com.kqinfo.universal.workflow.model.StartNode;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class StartNodeParser extends NodeParser {

	@Override
	protected WorkNode newNode() {
		return new StartNode();
	}

	@Override
	protected void parse(JSONObject nodeJson, WorkNode workNode) {
		StartNode startNode = (StartNode) workNode;
		final List<TransitionNode> transitionNodes = parseTransitionNode(nodeJson);
		startNode.setTransitionNodes(transitionNodes);
	}

}
