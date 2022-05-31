package com.kqinfo.universal.enums.core;

import java.util.Map;

/**
 * @author Zijian Liao
 * @since 2.5.0
 */
public final class KqEnumHandler {

    private KqEnumHandler() {
    }

    public static Map<String, Map<Object, Object>> getAll() {
        return KqEnumStorage.getEnumRepository();
    }

    public static Map<Object, Object> get(String enumName) {
        return KqEnumStorage.get(enumName);
    }
}
