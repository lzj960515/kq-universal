package com.kqinfo.universal.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待办任务
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "待办任务")
public class TodoTaskDto implements Serializable {

    private static final long serialVersionUID = -7890456488470887603L;

    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("跳转地址")
    private String callUri;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
