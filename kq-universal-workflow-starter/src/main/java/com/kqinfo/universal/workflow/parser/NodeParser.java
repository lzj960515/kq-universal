package com.kqinfo.universal.workflow.parser;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 节点解析器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class NodeParser {

	public WorkNode parse(JSONObject nodeJson) {
		final WorkNode workNode = newNode();
		workNode.setName(nodeJson.getStr("name")).setType(nodeJson.getStr("type"));
		workNode.setEvent(nodeJson.getStr("event"));
		parse(nodeJson, workNode);
		return workNode;
	}

	protected List<TransitionNode> parseTransitionNode(JSONObject nodeJson) {
		return parseTransitionNode(nodeJson, false);
	}

	protected List<TransitionNode> parseTransitionNode(JSONObject nodeJson, boolean needExpr) {
		final JSONArray transitions = nodeJson.getJSONArray("transitions");
		if(transitions == null){
			return Collections.emptyList();
		}
		List<TransitionNode> transitionNodes = new ArrayList<>(transitions.size());
		for (Object transition : transitions) {
			JSONObject transitionJson = (JSONObject) transition;
			TransitionNode transitionNode = new TransitionNode();
			transitionNode.setName(transitionJson.getStr("name")).setType(transitionJson.getStr("type"));
			transitionNode.setTo(transitionJson.getStr("to"));
			if (needExpr) {
				transitionNode.setExpression(transitionJson.getStr("expression"));
			}
			transitionNodes.add(transitionNode);
		}
		return transitionNodes;
	}

	/**
	 * 创建节点
	 * @return 节点
	 */
	protected abstract WorkNode newNode();

	/**
	 * 解析数据填充到节点中
	 * @param nodeJson 节点数据
	 * @param workNode 节点
	 */
	protected abstract void parse(JSONObject nodeJson, WorkNode workNode);

}
