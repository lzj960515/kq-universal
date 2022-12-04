package com.kqinfo.universal.yapi.domain;

import lombok.Data;

/**
 * 菜单信息
 *
 * @author Zijian Liao
 * @since 2.13.0
 */
@Data
public class CatInfo {

    private Long _id;
    private String name;
    private Long project_id;
    private String desc;
    private String uid;

}
