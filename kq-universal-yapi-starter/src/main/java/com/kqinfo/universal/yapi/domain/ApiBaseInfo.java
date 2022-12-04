package com.kqinfo.universal.yapi.domain;

import lombok.Data;

/**
 * api基本信息
 *
 * @author Zijian Liao
 * @since 2.13.0
 */
@Data
public class ApiBaseInfo {

    private Long _id;
    private String title;
    private String path;
    private String method;
    private Long project_id;
    private Long catid;
}
