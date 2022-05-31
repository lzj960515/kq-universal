package com.kqinfo.universal.workflow.handler;

import com.kqinfo.universal.workflow.core.Execution;
import com.kqinfo.universal.workflow.exception.WorkflowException;
import com.kqinfo.universal.workflow.model.DecisionNode;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Zijian Liao
 * @since 1.00
 */
@Component
public class DecisionHandler extends NodeHandler {

	ExpressionParser parser = new SpelExpressionParser();

	@Override
	public void execute(WorkNode workNode, Execution execution) {
		DecisionNode decisionNode = (DecisionNode) workNode;
		final Map<String, Object> variables = execution.getVariables();
		if (StringUtils.hasText(decisionNode.getExpression())) {
			String nextNode = parse(String.class, decisionNode.getExpression(), variables);
			if (StringUtils.hasText(nextNode)) {
				// 找到下一个节点
				final WorkNode node = workflowHandler.getNode(nextNode, execution.getWorkNodes());
				workflowHandler.execute(node, execution);
				return;
			}
		}
		final List<TransitionNode> transitionNodes = decisionNode.getTransitionNodes();
		for (TransitionNode transitionNode : transitionNodes) {
			if (StringUtils.hasText(transitionNode.getExpression())) {
				Boolean find = parse(Boolean.class, transitionNode.getExpression(), variables);
				if (Boolean.TRUE.equals(find)) {
					final WorkNode node = workflowHandler.getNode(transitionNode.getTo(), execution.getWorkNodes());
					workflowHandler.execute(node, execution);
					return;
				}
			}
		}
		throw new WorkflowException(execution.getProcessInstance().getId() + "->decision无法确定下一个节点");
	}

	public <T> T parse(Class<T> resultType, String expr, Map<String, Object> args) {
		EvaluationContext context = new StandardEvaluationContext();
		for (Map.Entry<String, Object> entry : args.entrySet()) {
			context.setVariable(entry.getKey(), entry.getValue());
		}
		return parser.parseExpression(expr).getValue(context, resultType);
	}

}
