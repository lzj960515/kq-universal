package com.kqinfo.universal.yapi.domain;

import lombok.Data;

/**
 * @author Zijian Liao
 * @since 2.13.0
 */
@Data
public class ReqParam {
    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数备注
     */
    private String desc;
}
