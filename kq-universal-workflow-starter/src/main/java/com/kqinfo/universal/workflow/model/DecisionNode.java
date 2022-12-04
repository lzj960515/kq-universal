package com.kqinfo.universal.workflow.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 条件节点
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DecisionNode extends WorkNode {

    @ApiModelProperty(value = "条件表达式, 用于计算出下一个节点的名称")
    private String expression;

}
