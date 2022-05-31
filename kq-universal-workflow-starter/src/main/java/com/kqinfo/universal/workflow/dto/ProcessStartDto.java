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
@ApiModel(description = "启动流程请求参数")
public class ProcessStartDto implements Serializable {

    private static final long serialVersionUID = -8461188802792105446L;

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("流程定义名称")
    private String processDefName;

    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("流程发起人：用户id")
    private String creator;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("流程变量")
    private Map<String, Object> variables;

    @ApiModelProperty("下一任务节点的受理人列表, 不指定则用流程定义的默认配置")
    private List<Assignee> nextTaskAssigneeList;
}
