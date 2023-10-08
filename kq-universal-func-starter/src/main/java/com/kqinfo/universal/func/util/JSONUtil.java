package com.kqinfo.universal.func.util;

import cn.hutool.core.lang.Pair;
import com.alibaba.fastjson.JSONObject;
import com.kqinfo.universal.func.core.entity.FieldInfo;
import org.springframework.util.StringUtils;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public final class JSONUtil {

    /**
     * 获取映射字段值
     *
     * @param sourceJson 值对象
     * @param mapping    字段映射
     * @param index      当前索引
     * @return {@link Object}
     * @author YangXiaoLong
     * 2023/5/15 14:54
     */
    public static Object getValue(JSONObject sourceJson, String mapping, int index) {
        // 解析mapping字段
        final Pair<String, FieldInfo> pair = parseMappingField(mapping);
        final FieldInfo fieldInfo = pair.getValue();
        final String next = pair.getKey();
        // 判断是否有子字段
        if (StringUtils.hasText(next)) {
            // 有子字段，需要递归取值
            // 如果是数组，sourceJson为其中一个元素
            if (fieldInfo.isArray()) {
                return getValue((JSONObject) sourceJson.getJSONArray(fieldInfo.getName()).get(index), next, index);
            }
            return getValue(sourceJson.getJSONObject(fieldInfo.getName()), next, index);
        }
        // 直接取值
        if (fieldInfo.isArray()) {
            return sourceJson.getJSONArray(fieldInfo.getName());
        }
        return sourceJson.get(fieldInfo.getName());
    }

    /**
     * 解析映射字段
     * ex:[patients.idNumber
     *
     * @param field 字段
     * @return {@link Pair<>}
     * @author YangXiaoLong
     * 2023/5/15 14:40
     */
    public static Pair<String, FieldInfo> parseMappingField(String field) {
        // 得到当前字段名称，是否数组，下个字段
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setArray(field.startsWith("["));

        int start = field.startsWith("#") || field.startsWith("[") || field.startsWith(".") ? 1 : 0;

        // user.name  #user.name user#name.value name .name
        final int index1 = field.indexOf('[', 1);
        final int index2 = field.indexOf('.', 1);
        final int index3 = field.indexOf('#', 1);
        // 去掉所有-1的，取最小的那个
        int end = filter(filter(index1, index2), index3);
        end = end == -1 ? field.length() : end;
        fieldInfo.setName(field.substring(start, end));
        // 下一截
        String next = field.substring(end);
        return Pair.of(next, fieldInfo);
    }

    private static int filter(int index1 , int index2){
        return index1 > 0 && index2 > 0 ? Math.min(index1, index2) : Math.max(index1, index2);
    }
}
