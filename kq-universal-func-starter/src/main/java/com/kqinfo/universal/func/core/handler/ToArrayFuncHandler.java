package com.kqinfo.universal.func.core.handler;

import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class ToArrayFuncHandler extends ObjectFuncHandler {

    @Override
    protected Object doHandle(List<Object> analyticRes) {
        return analyticRes;
    }

    @Override
    protected int getMethodParamSize() {
        return -1;
    }

    @Override
    public String funcName() {
        return "toArray";
    }

    @Override
    public String description() {
        return "将字符串以逗号分隔，转化为列表";
    }


}
