package com.kqinfo.universal.yapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 解析结果
 * @author Zijian Liao
 * @since 2.13.0
 */
@Data
@AllArgsConstructor
public class ControllerInfo {

    private String name;

    private List<ApiInfo> apiInfoList;
}
