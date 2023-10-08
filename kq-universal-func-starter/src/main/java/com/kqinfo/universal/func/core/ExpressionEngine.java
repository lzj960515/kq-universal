package com.kqinfo.universal.func.core;

import com.alibaba.fastjson.JSONArray;
import com.kqinfo.universal.func.core.entity.ExpressionData;
import com.kqinfo.universal.func.core.entity.FunctionInfo;
import com.kqinfo.universal.func.core.handler.abs.ArrayFuncHandler;
import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import com.kqinfo.universal.func.util.JSONUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表达式引擎
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class ExpressionEngine implements InitializingBean {

    public static List<FunctionInfo> expressionInterpretationCache = new ArrayList<>();

    private static final String VALUE_IDENTIFICATION = "#";
    private static final String ARRAY_IDENTIFICATION = "[";
    @Resource
    private List<ObjectFuncHandler> objectHandlers;
    @Resource
    private List<ArrayFuncHandler> arrayHandlers;

    private static final Map<String, ObjectFuncHandler> OBJECT_EXPRESS_HANDLER_MAP = new HashMap<>();
    private static final Map<String, ArrayFuncHandler> ARRAY_EXPRESS_HANDLER_MAP = new HashMap<>();

    /**
     * 计算表达式值
     *
     * @param expressionData 表达式
     * @return {@link Object}
     * @author YangXiaoLong
     * 2023/5/15 14:54
     */
    public Object evaluateExpression(ExpressionData expressionData) {
        final ObjectFuncHandler expressHandler = OBJECT_EXPRESS_HANDLER_MAP.get(findMethod(expressionData.getExpression()));
        if (expressHandler != null) {
            return expressHandler.handle(expressionData);
        }
        return evaluateValue(expressionData);
    }

    public Object evaluateValue(ExpressionData expressionData){
        final String expression = expressionData.getExpression();
        // 如果#或[开头,则从Data中取
        if (expression.startsWith(VALUE_IDENTIFICATION) || expression.startsWith(ARRAY_IDENTIFICATION)) {
            // 获取值操作
            return JSONUtil.getValue(expressionData.getData(), expression, expressionData.getIndex());
        }
        if(expression.startsWith("'") && expression.endsWith("'")){
            return expression.substring(1, expression.length()-1);
        }
        return expression;
    }

    public Object evaluateArrayExpression(JSONArray array, String expression) {
        // 处理空字符串
        expression = expression.replaceAll("\\s+", "");
        final ArrayFuncHandler expressHandler = ARRAY_EXPRESS_HANDLER_MAP.get(findMethod(expression));
        if (expressHandler != null) {
            return expressHandler.handle(array, expression);
        }
        throw new RuntimeException("未找到对应的表达式:" + expression);
    }

    /**
     * 获取方法函数
     *
     * @param expression 表达式
     * @return {@link String}
     * @author YangXiaoLong
     * 2023/5/8 9:38
     */
    public String findMethod(String expression) {
        final int i = expression.indexOf('(');
        if (i > 0) {
            return expression.substring(0, i).toLowerCase(Locale.ROOT);
        }
        return null;
    }

    public List<FunctionInfo> expressionInterpretation() {
        return expressionInterpretationCache;
    }

    @Override
    public void afterPropertiesSet() {
        for (ObjectFuncHandler handler : objectHandlers) {
            OBJECT_EXPRESS_HANDLER_MAP.put(handler.funcName().toLowerCase(Locale.ROOT), handler);
        }
        for (ArrayFuncHandler handler : arrayHandlers) {
            ARRAY_EXPRESS_HANDLER_MAP.put(handler.funcName().toLowerCase(Locale.ROOT), handler);
        }
        List<FunctionInfo> objectExpress = objectHandlers.stream().map(handler -> {
            FunctionInfo fe = new FunctionInfo();
            fe.setName(handler.funcName());
            fe.setDescription(handler.description());
            return fe;
        }).collect(Collectors.toList());
        List<FunctionInfo> arrayExpress = arrayHandlers.stream().map(handler -> {
            FunctionInfo fe = new FunctionInfo();
            fe.setName(handler.funcName());
            fe.setDescription(handler.description());
            return fe;
        }).collect(Collectors.toList());
        expressionInterpretationCache.addAll(objectExpress);
        expressionInterpretationCache.addAll(arrayExpress);
    }
}
