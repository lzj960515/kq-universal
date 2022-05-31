package com.kqinfo.universal.workflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 流程节点
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ProcessNode extends BaseNode {

	private List<WorkNode> workNodes;

	private List<TaskNode> taskNodes;

}
