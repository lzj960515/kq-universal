package com.kqinfo.universal.common.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分页参数
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class PageParam implements Serializable {

    private static final long serialVersionUID = -5946600583467786752L;

    @ApiModelProperty("页码")
    private int current = 1;

    @ApiModelProperty("笔数")
    private int size = 10;
}
