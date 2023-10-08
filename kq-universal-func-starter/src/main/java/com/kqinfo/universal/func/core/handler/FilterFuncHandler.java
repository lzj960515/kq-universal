package com.kqinfo.universal.func.core.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kqinfo.universal.func.core.entity.ExpressionData;
import com.kqinfo.universal.func.core.handler.abs.ArrayFuncHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class FilterFuncHandler extends ArrayFuncHandler {

    @Override
    protected Object doHandle(JSONArray array, List<String> parameters) {
        final List<Object> filterResult = array.stream().filter(item -> {
            Object result = expressionEngine.evaluateExpression(new ExpressionData(parameters.get(0), (JSONObject) item, 0));
            return Boolean.TRUE.equals(result);
        }).collect(Collectors.toList());
        // 返回排序结果
        return JSONArray.parseArray(JSON.toJSONString(filterResult));
    }

    @Override
    protected int getMethodParamSize() {
        return 1;
    }

    @Override
    public String funcName() {
        return "filter";
    }

    @Override
    public String description() {
        return "根据条件筛选列表数据，参数为布尔值, 例：filter(gt(#age,10))";
    }
}
