package com.kqinfo.universal.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务日志
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "审核日志")
public class TaskLogDto implements Serializable {

    private static final long serialVersionUID = 1045908086305661714L;

    @ApiModelProperty("审核人id")
    private String operatorId;

    @ApiModelProperty("审核人")
    private String operator;

    @ApiModelProperty("审核原因")
    private String reason;

    @ApiModelProperty("审核时间")
    private LocalDateTime createTime;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("审核状态")
    private Integer status;
}
