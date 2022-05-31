package com.kqinfo.universal.workflow.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 历史流程任务
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("tbl_history_task")
@ApiModel(value = "HistoryTask对象", description = "历史流程任务")
public class HistoryTask implements Serializable {

	private static final long serialVersionUID = -6531951179926166865L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "任务名称")
	private String name;

	@ApiModelProperty(value = "父级任务id, 第一个任务则为start:0")
	private Long parentId;

	@ApiModelProperty(value = "流程定义id")
	private Long processId;

	@ApiModelProperty(value = "流程实例id")
	private Long instanceId;

	@ApiModelProperty(value = "租户id")
	private Long tenantId;

	@ApiModelProperty(value = "业务id")
	private String businessId;

	@ApiModelProperty(value = "页面跳转地址")
	private String callUri;

	@ApiModelProperty(value = "任务处理人id")
	private String operatorId;

	@ApiModelProperty(value = "任务处理人")
	private String operatorName;

	@ApiModelProperty(value = "1.提交审核 2.审核通过 3.驳回")
	private Integer status;

	@ApiModelProperty(value = "原因")
	private String reason;

	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

}