package com.kqinfo.universal.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "待办事项分页")
public class TodoTaskPageDto implements Serializable {

    private static final long serialVersionUID = 6956526268192582849L;

    @ApiModelProperty("审核名称")
    private String approveName;

    @ApiModelProperty("审核描述")
    private String approveDesc;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("任务")
    private String task;

    @ApiModelProperty("审核阶段")
    private String approveStage;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("跳转地址")
    private String callUri;
}
