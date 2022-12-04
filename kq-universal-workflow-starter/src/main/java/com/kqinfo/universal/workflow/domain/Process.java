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
 * 流程定义
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("tbl_process")
@ApiModel(value = "Process对象", description = "流程定义")
public class Process implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "流程定义名称")
    private String name;

    @ApiModelProperty(value = "流程定义描述")
    private String description;

    @ApiModelProperty(value = "流程定义的内容")
    private String context;

    @ApiModelProperty(value = "流程定义的版本，每次创建同名称的流程定义，版本+1")
    private Integer version;

    @ApiModelProperty(value = "签名")
    private String sign;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}