package com.kqinfo.universal.func.core.handler;

import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kqinfo.universal.func.core.entity.ExpressionData;
import com.kqinfo.universal.func.core.handler.abs.ArrayFuncHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class SortFuncHandler extends ArrayFuncHandler {

    @Override
    protected Object doHandle(JSONArray array, List<String> parameters) {
        // 构建排序规则
        List<SortRule> sortRuleList = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++, i++) {
            sortRuleList.add(new SortRule(parameters.get(i), parameters.get(i+1)));
        }
        // 进行排序
        final List<Object> sortResult = array.stream().sorted((cur, next) -> sort((JSONObject) cur,
                (JSONObject) next, sortRuleList)).collect(Collectors.toList());
        // 返回排序结果
        return JSONArray.parseArray(JSON.toJSONString(sortResult));
    }

    @Override
    protected int getMethodParamSize() {
        return -1;
    }

    @Override
    public String funcName() {
        return "sort";
    }

    @Override
    public String description() {
        return "列表排序，参数格式为：#字段，排序方式，#字段，排序方式,....排序方式有三类：asc,desc,自定义排序数组(sql中的order by field)";
    }


    private int sort(JSONObject curJson, JSONObject nextJson, List<SortRule> sortRuleList){
        int cmp = 0;
        for (SortRule sortRule : sortRuleList) {
            final Object rule = expressionEngine.evaluateExpression(new ExpressionData(sortRule.getRule(), curJson, 0));
            final Object curValue = expressionEngine.evaluateExpression(new ExpressionData(sortRule.getField(), curJson, 0));
            final Object nextValue = expressionEngine.evaluateExpression(new ExpressionData(sortRule.getField(), nextJson, 0));
            if("desc".equals(rule) || "asc".equals(rule)){
                cmp = sampleSort(curValue.toString(), nextValue.toString(), "asc".equals(rule));
            }else {
                cmp = customSort(curValue.toString(), nextValue.toString(), JSON.parseArray(JSON.toJSONString(rule), String.class));
            }
            // 如果已经排序出结果，则直接返回
            if(cmp != 0){
                return cmp;
            }
        }
        return cmp;
    }

    private int sampleSort(String curValue, String nextValue, boolean asc){
        // 时间？数字？字符串？
        if(NumberUtil.isNumber(curValue)){
            final BigDecimal bigDecimal = new BigDecimal(curValue);
            int cmp = bigDecimal.compareTo(new BigDecimal(nextValue));
            return asc ? cmp : -cmp;
        }
        try{
            // 判断是否为时间
            DateTime dateTime = DateUtil.parseDate(curValue);
            int cmp =  dateTime.compareTo(DateUtil.parseDate(nextValue));
            return asc ? cmp : -cmp;
        }catch (DateException ignore){}
        // 字符串
        int cmp = curValue.compareTo(nextValue);
        return asc ? cmp : -cmp;
    }

    /**
     * 根据自定义字符串顺序对数据排序
     * @param sortStr 字符串列表
     * @param curValue 当前值
     * @param nextValue 下一个值
     * @return 比较值在字符串顺序，在前则返回-1；相等则返回0；否则返回1
     */
    private int customSort(String curValue, String nextValue, List<String> sortStr){
        // 将字符串数组转化为Map，key为字符串，value为该字符串的索引
        Map<String, Integer> map = new HashMap<>(sortStr.size());

        for (int i = 0; i < sortStr.size(); i++) {
            map.put(sortStr.get(i), i);
        }
        // 获取当前字符串和下一个字符串在排序数组中的索引
        Integer cur = map.get(curValue);
        if (cur == null) {
            // 当前字符串不存在于排序数组中，则默认为最小值
            return 1;
        }
        Integer next = map.get(nextValue);
        if (next == null) {
            // 下一个字符串不存在于排序数组中，则默认为最大值
            return -1;
        }
        // 比较当前字符串和下一个字符串的索引，返回比较结果
        return cur.compareTo(next);
    }

    @Data
    @AllArgsConstructor
    static class SortRule {

        private String field;

        private String rule;
    }
}
