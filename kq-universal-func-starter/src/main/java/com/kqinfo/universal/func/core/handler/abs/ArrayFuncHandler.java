package com.kqinfo.universal.func.core.handler.abs;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class ArrayFuncHandler extends FuncHandler {

    public Object handle(JSONArray array, String expression) {
        final List<String> parameters = extractParameters(getFuncLen(), expression);
        checkParameterSize(parameters.size());
        return doHandle(array, parameters);
    }

    protected abstract Object doHandle(JSONArray array, List<String> parameters);
}
