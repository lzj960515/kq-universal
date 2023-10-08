package com.kqinfo.universal.func.core.handler;

import cn.hutool.core.util.NumberUtil;
import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class LtFuncHandler extends ObjectFuncHandler {

    @Override
    protected Object doHandle(List<Object> analyticRes) {
        return NumberUtil.isLess(new BigDecimal(analyticRes.get(0).toString()), new BigDecimal(analyticRes.get(1).toString()));
    }

    @Override
    protected int getMethodParamSize() {
        return 2;
    }

    /**
     * 表达式 LT(#age,10)
     */
    @Override
    public String funcName() {
        return "LT";
    }

    @Override
    public String description() {
        return "两个值进行比较, 如果第一个值小于第二个值,则返回true,否则返回false";
    }
}
