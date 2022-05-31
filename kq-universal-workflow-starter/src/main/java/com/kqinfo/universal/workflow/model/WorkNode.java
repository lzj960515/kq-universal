package com.kqinfo.universal.workflow.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 工作节点
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WorkNode extends BaseNode {

	private List<TransitionNode> transitionNodes;

	/**
	 * @see com.kqinfo.universal.workflow.core.WorkflowEvent
	 */
	@ApiModelProperty("事件名称")
	private String event;
}
