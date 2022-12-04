package com.kqinfo.universal.yapi.generator;

import com.kqinfo.universal.yapi.domain.ApiInfo;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class Swagger2Parser extends AbstractParser{

    @Override
    boolean isSupport(Class<?> cls) {
        return false;
    }

    @Override
    String parseCatInfo(Class<?> cls) {
        return null;
    }

    @Override
    List<ApiInfo> parseApiInfo(Class<?> cls) {
        return null;
    }
}
