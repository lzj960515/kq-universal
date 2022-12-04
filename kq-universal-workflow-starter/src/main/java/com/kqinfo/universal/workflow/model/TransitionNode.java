package com.kqinfo.universal.workflow.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 变迁节点
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TransitionNode extends BaseNode {

    @ApiModelProperty(value = "目标节点")
    private String to;

    @ApiModelProperty(value = "条件表达式")
    private String expression;

}
