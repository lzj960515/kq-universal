package com.kqinfo.universal.workflow.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 任务节点， 包含受理人，角色字段 受理人：可以用于存放管理员id 角色：存储角色的标识 两者结合做到一个任务节点平时由指定的角色受理，管理员进行兜底
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TaskNode extends WorkNode {

	@ApiModelProperty("受理人")
	private String assignee;

	@ApiModelProperty("角色")
	private String role;

	@ApiModelProperty("顺序")
	private Integer sequence;

	@ApiModelProperty("回调地址")
	private String callUri;

}
