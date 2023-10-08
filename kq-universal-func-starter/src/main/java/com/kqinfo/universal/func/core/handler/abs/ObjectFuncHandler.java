package com.kqinfo.universal.func.core.handler.abs;

import com.kqinfo.universal.func.core.entity.ExpressionData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class ObjectFuncHandler extends FuncHandler {

    public Object handle(ExpressionData expressionData) {
        // 获取函数中的参数
        final List<String> parameters = extractParameters(getFuncLen(), expressionData.getExpression());
        checkParameterSize(parameters.size());
        List<Object> analyticRes = new ArrayList<>(parameters.size());
        for (String parameter : parameters) {
            // 继续递归执行每个参数(参数可能是个函数)
            Object val = expressionEngine.evaluateExpression(ExpressionData.of(parameter, expressionData));
            analyticRes.add(val);
        }
        return doHandle(analyticRes);
    }


    /**
     * 处理表达式的结果
     * -每一个表达式处理结果方式不同交由子类实现
     *
     */
    protected abstract Object doHandle(List<Object> analyticRes);
}
