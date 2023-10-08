package com.kqinfo.universal.func.core.handler.abs;

import com.kqinfo.universal.func.core.ExpressionEngine;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 表达式解析处理器基类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public abstract class FuncHandler {

    @Autowired
    protected ExpressionEngine expressionEngine;

    /**
     * 校验参数长度是否相等
     *
     */
    protected void checkParameterSize(int size) {
        if (getMethodParamSize() != -1 && size != getMethodParamSize()) {
            throw new RuntimeException("表达式参数不匹配");
        }
    }

    /**
     * 提取参数集合
     *
     * @param openParenthesisIndex 左括号下标, 函数名的长度就是左括号的下标位置
     * @param expression           当前表达式
     */
    protected List<String> extractParameters(int openParenthesisIndex, String expression) {
        // 找到最右边的括号
        int closingParenthesisIndex = findClosingParenthesis(expression, openParenthesisIndex);
        // 去除两边的括号，得到函数内的字符串
        String content = expression.substring(openParenthesisIndex + 1, closingParenthesisIndex);

        List<String> parameters = new ArrayList<>();
        // 参数起始下标，括号计数
        int parameterStartIndex = 0;
        int parenthesisCount = 0;

        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (ch == '(') {
                parenthesisCount++;
            } else if (ch == ')') {
                parenthesisCount--;
            } else if (ch == ',' && parenthesisCount == 0) {
                // 当遇到逗号，且括号成对，说明逗号不在嵌套函数内，记录该参数
                parameters.add(content.substring(parameterStartIndex, i));
                // 修改起始值
                parameterStartIndex = i + 1;
            }
        }
        // 保存最后一个的参数
        parameters.add(content.substring(parameterStartIndex));

        return parameters;
    }

    /**
     * 查询当前表达式的有括号索引
     *
     * @param expression           表达式
     * @param openParenthesisIndex 方法长度
     * @return {@link int}
     * @author YangXiaoLong
     * 2023/5/8 9:13
     */
    protected int findClosingParenthesis(String expression, int openParenthesisIndex) {
        int counter = 1;
        for (int i = openParenthesisIndex + 1; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == '(') {
                counter++;
            } else if (ch == ')') {
                counter--;

                if (counter == 0) {
                    return i;
                }
            }
        }
        //IF(IN(#idNo),8,IF(EQ(LEN(#patCardNo),10),2,6)
        throw new IllegalArgumentException("没有匹配的括号:" + expression);
    }

    /**
     * 获取函数名称长度
     *
     * @return 函数名称长度
     */
    protected int getFuncLen() {
        return funcName().length();
    }

    /**
     * 获取方法参数个数
     *
     * @return 方法参数个数
     */
    protected abstract int getMethodParamSize();

    /**
     * 函数名称
     *
     * @return 函数名称
     */
    public abstract String funcName();

    /**
     * 函数描述
     *
     * @return 函数描述
     */
    public abstract String description();
}
