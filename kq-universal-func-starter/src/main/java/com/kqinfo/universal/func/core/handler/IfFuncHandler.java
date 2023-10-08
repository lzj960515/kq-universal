package com.kqinfo.universal.func.core.handler;

import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class IfFuncHandler extends ObjectFuncHandler {

    @Override
    protected Object doHandle(List<Object> analyticRes) {
        Boolean booleanValue = (Boolean) analyticRes.get(0);
        if (Boolean.TRUE.equals(booleanValue)) {
            return analyticRes.get(1);
        }
        return analyticRes.get(2);
    }

    @Override
    protected int getMethodParamSize() {
        return 3;
    }

    /**
     * 表达式IF(NN(age),five,other)
     * 输出:five
     *
     * @return {@link String}
     * @author YangXiaoLong
     * 2023/5/8 9:42
     */
    @Override
    public String funcName() {
        return "IF";
    }

    @Override
    public String description() {
        return "判断一个条件是否满足:如果满足返回一个值,如果不满足则返回另外一个值";
    }
}
