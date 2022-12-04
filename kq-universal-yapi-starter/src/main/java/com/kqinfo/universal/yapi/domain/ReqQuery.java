package com.kqinfo.universal.yapi.domain;

import lombok.Data;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
@Data
public class ReqQuery {
    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数备注
     */
    private String desc;
    /**
     * 参数示例
     */
    private String example;
    /**
     * 是否必须
     */
    private Integer required;
    /**
     * 表单请求时使用，text, file
     */
    private String type;
}
