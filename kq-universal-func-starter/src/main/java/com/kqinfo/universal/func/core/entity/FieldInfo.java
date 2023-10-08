package com.kqinfo.universal.func.core.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 字段信息
 *
 * @author YangXiaoLong
 * @since 2023/5/10 10:06
 */
@Data
public class FieldInfo {
    /**
     * 字段名
     */
    private String name;
    /**
     * 字段映射
     */
    private String mapping;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 是否为数据 true:是 false:不是
     */
    private boolean isArray;
    /**
     * 子节点
     */
    private Map<String, FieldInfo> children = new HashMap<>();
}
