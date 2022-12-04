package com.kqinfo.universal.workflow.core;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kqinfo.universal.workflow.constant.NodeEnum;
import com.kqinfo.universal.workflow.exception.WorkflowException;
import com.kqinfo.universal.workflow.handler.NodeHandler;
import com.kqinfo.universal.workflow.model.TaskNode;
import com.kqinfo.universal.workflow.model.TransitionNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import com.kqinfo.universal.workflow.parser.NodeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 工作流处理器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class WorkflowHandler {

    private final Map<String, NodeParser> nodeParserMap;

    @Autowired
    private Map<String, NodeHandler> nodeHandlerMap;
    /**
     * 增加缓存以提高性能
     */
    private final Map<String, List<WorkNode>> nodeCache = new ConcurrentHashMap<>();
    private final MD5 md5 = new MD5();

    public List<WorkNode> parse(String context) {
        final String cacheKey = md5.digestHex(context);
        List<WorkNode> workNodes = nodeCache.get(cacheKey);
        if (workNodes != null) {
            return workNodes;
        }
        final JSONObject processJson = JSONUtil.parseObj(context);
        // 节点名称
        final JSONArray nodes = processJson.getJSONArray("nodes");
        List<WorkNode> nodeList = new ArrayList<>(nodes.size());
        for (Object node : nodes) {
            JSONObject nodeJson = (JSONObject) node;
            // 节点类型
            final String nodeType = nodeJson.getStr("type");
            final WorkNode workNode = getParser(nodeType).parse(nodeJson);
            nodeList.add(workNode);
        }
        nodeCache.put(cacheKey, nodeList);
        return nodeList;
    }

    private NodeParser getParser(String nodeType) {
        String nodeParserName = nodeType + "NodeParser";
        return nodeParserMap.get(nodeParserName);
    }

    public List<TaskNode> getTaskNodes(List<WorkNode> workNodes) {
        return workNodes.stream().filter(node -> NodeEnum.TASK.name().equalsIgnoreCase(node.getType()))
                .map(node -> (TaskNode) node).collect(Collectors.toList());
    }

    public WorkNode getStartNode(List<WorkNode> workNodes) {
        final Optional<WorkNode> optional = workNodes.stream()
                .filter(node -> NodeEnum.START.name().equalsIgnoreCase(node.getType())).findFirst();
        if (!optional.isPresent()) {
            throw new WorkflowException("未找到开始节点");
        }
        return optional.get();
    }

    public String getCallUri(String context, String businessId) {
        final JSONObject processJson = JSONUtil.parseObj(context);
        final String callUri = processJson.getStr("callUri");
        return parseCallUri(callUri, businessId);
    }

    public String parseCallUri(String callUri, String businessId) {
        // 如果没有配回调地址就返回空字符串
        if (StringUtils.isEmpty(callUri)) {
            return "";
        }
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(callUri);
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("businessId", businessId);
        return expression.getValue(evaluationContext, String.class);
    }

    public void start(Execution execution) {
        final List<WorkNode> workNodes = execution.getWorkNodes();
        final WorkNode startNode = getStartNode(workNodes);
        execute(startNode, execution);
    }

    /**
     * @param nodeName  当前节点名称
     * @param execution 执行对象
     */
    public void execute(String nodeName, Execution execution) {
        final List<WorkNode> workNodes = execution.getWorkNodes();
        // 找到该节点
        final WorkNode workNode = getNode(nodeName, workNodes);
        // 找到下一个节点
        final List<TransitionNode> transitionNodes = workNode.getTransitions();
        // 一般只有一个
        for (TransitionNode transitionNode : transitionNodes) {
            final WorkNode node = getNode(transitionNode.getTo(), workNodes);
            execute(node, execution);
        }
    }

    public WorkNode getNode(String nodeName, List<WorkNode> workNodes) {
        final Optional<WorkNode> optional = workNodes.stream().filter(node -> node.getName().equals(nodeName))
                .findFirst();
        if (!optional.isPresent()) {
            throw new WorkflowException("未找到任务节点");
        }
        return optional.get();
    }

    public void execute(WorkNode workNode, Execution execution) {
        // 根据节点类型找到处理器，如果是任务节点就创建任务，如果是开始节点就继续执行，如果是条件节点就判断条件后继续执行，如果是结束节点就结束流程
        getNodeHandler(workNode.getType()).execute(workNode, execution);
    }

    public NodeHandler getNodeHandler(String nodeType) {
        String nodeHandlerName = nodeType + "Handler";
        return nodeHandlerMap.get(nodeHandlerName);
    }

}
