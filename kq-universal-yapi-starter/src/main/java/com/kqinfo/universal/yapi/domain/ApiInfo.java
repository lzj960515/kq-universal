package com.kqinfo.universal.yapi.domain;

import lombok.Data;

import java.util.List;

/**
 * 接口信息
 * @author Zijian Liao
 * @since 2.13.0
 */
@Data
public class ApiInfo {

    /**
     * 项目token
     */
    private String token;
    /**
     * 接口名称
     */
    private String title;
    private String desc;
    private String path;
    /**
     * GET POST PUT DELETE
     */
    private String method;
    private Long catid;
    /**
     * 状态，未完成：undone 完成：done
     */
    private String status;
    /**
     * 请求参数类型：json,raw,form
     */
    private String req_body_type;
    /**
     * 请求参数
     */
    private List<ReqQuery> req_query;
    /**
     * 路径参数
     */
    private List<ReqParam> req_params;

    private List<ReqQuery> req_body_form;
    /**
     * json请求参数
     */
    private String req_body_other;
    /**
     * 请求头
     */
    private List<ReqHeader> req_headers;
    /**
     * 响应数据类型：json
     */
    private String res_body_type;
    /**
     * 响应数据 json
     */
    private String res_body;

}
