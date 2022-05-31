package com.kqinfo.universal.workflow.model;

import com.kqinfo.universal.workflow.constant.NodeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础节点
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class BaseNode {

	@ApiModelProperty("节点名称")
	private String name;

	/**
	 * @see NodeEnum
	 */
	@ApiModelProperty("节点类型")
	private String type;

}
