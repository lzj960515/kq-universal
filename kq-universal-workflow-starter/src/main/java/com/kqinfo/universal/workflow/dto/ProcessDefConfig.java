package com.kqinfo.universal.workflow.dto;

import com.kqinfo.universal.workflow.model.WorkNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 流程定义
 *
 * @author Zijian Liao
 * @since 2.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "流程定义配置")
public class ProcessDefConfig {

    @ApiModelProperty("流程定义名称，启动流程使用，比如请假审批，这里可以写leave-approve")
    private String name;

    @ApiModelProperty("流程定义描述，展示流程使用，比如请假审批，这里可以写请假审批")
    private String desc;

    @ApiModelProperty("回调uri")
    private String callUri;

    @ApiModelProperty("节点, 请注意，WorkNode为所有节点的父类，创建对象时请使用实际的Node, 如StartNode")
    private List<WorkNode> nodes;
}
