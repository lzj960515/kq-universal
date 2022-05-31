package com.kqinfo.universal.enums.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 枚举字段存储
 *
 * @author Zijian Liao
 * @since 2.5.0
 */
public final class KqEnumStorage {
    private KqEnumStorage() {
    }

    /**
     * {enumName:{code:desc}}
     */
    private static final Map<String, Map<Object, Object>> ENUM_REPOSITORY = new ConcurrentHashMap<>(16);

    public static void put(String enumName, Map<Object, Object> enumMap) {
        ENUM_REPOSITORY.put(enumName, enumMap);
    }

    public static Map<Object, Object> get(String enumName) {
        return ENUM_REPOSITORY.get(enumName);
    }

    public static Map<String, Map<Object, Object>> getEnumRepository() {
        return ENUM_REPOSITORY;
    }
}
