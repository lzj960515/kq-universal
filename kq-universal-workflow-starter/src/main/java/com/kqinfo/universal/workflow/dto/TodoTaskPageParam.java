package com.kqinfo.universal.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@ApiModel("分页参数")
public class TodoTaskPageParam implements Serializable {

    private static final long serialVersionUID = -986820134168829238L;

    private int current = 1;

    private int size = 10;

    @ApiModelProperty("流程定义名称")
    private String processDefName;

    @ApiModelProperty("开始时间")
    private LocalDate startDate;

    @ApiModelProperty("结束时间")
    private LocalDate endDate;

    @ApiModelProperty("任务描述")
    private String task;
}
