package com.kqinfo.universal.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "执行任务请求参数")
public class ExecuteTaskDto implements Serializable {

    private static final long serialVersionUID = -1858267711396820461L;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("流程定义名称")
    private String processDefName;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("用户id")
    private String operator;

    @ApiModelProperty("原因, 驳回任务时不能为空")
    private String reason;

    @ApiModelProperty("流程变量")
    private Map<String, Object> variables;

    @ApiModelProperty("下一任务节点的受理人列表, 不指定则用流程定义的默认配置")
    private List<Assignee> nextTaskAssigneeList;
}
