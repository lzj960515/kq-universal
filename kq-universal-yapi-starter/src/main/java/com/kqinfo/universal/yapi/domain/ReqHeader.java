package com.kqinfo.universal.yapi.domain;

import lombok.Data;

/**
 * 请求头
 *
 * @author Zijian Liao
 * @since 2.13.0
 */
@Data
public class ReqHeader {

    /**
     * 名称
     */
    private String name;
    /**
     * 参数值 multipart/form-data application/json
     */
    private String value;
    /**
     * 备注
     */
    private String desc;
    /**
     * 示例
     */
    private String example;
    /**
     * 是否必须
     */
    private Integer required;

    public static ReqHeader applicationJson(){
        ReqHeader reqHeader = new ReqHeader();
        reqHeader.setName("Content-Type");
        reqHeader.setValue("application/json");
        reqHeader.setRequired(1);
        return reqHeader;
    }

    public static ReqHeader form(){
        ReqHeader reqHeader = new ReqHeader();
        reqHeader.setName("Content-Type");
        reqHeader.setValue("multipart/form-data");
        reqHeader.setRequired(1);
        return reqHeader;
    }
}
