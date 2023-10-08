package com.kqinfo.universal.func.core.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 表达式数据
 *
 * @author YangXiaoLong
 * @since 2023/5/15 14:12
 */
@Data
public class ExpressionData {
    /**
     * 表达式
     */
    private String expression;
    /**
     * 取值数据
     */
    private JSONObject data;
    /**
     * 数据索引, 数组时使用，用于分辨取当前数组中的哪一下标
     */
    private Integer index;

    public ExpressionData(String expression, JSONObject data, int index){
        this.expression = expression.trim();
        this.data = data;
        this.index = index;
    }

    public ExpressionData(String expression, JSONObject data){
        this(expression, data, 0);
    }

    public static ExpressionData of(String expression, JSONObject data){
        return new ExpressionData(expression, data);
    }

    public static ExpressionData of(String expression, ExpressionData expressionData){
        return new ExpressionData(expression, expressionData.getData(), expressionData.getIndex());
    }

}
