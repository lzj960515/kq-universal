package com.kqinfo.universal.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "审核进度")
public class ApproveProgressDto implements Serializable {

    private static final long serialVersionUID = 7520504752474383150L;

    @ApiModelProperty("节点名称")
    private String pointName;

    @ApiModelProperty("审核完成 1.是 0.否")
    private Integer approved;
}
