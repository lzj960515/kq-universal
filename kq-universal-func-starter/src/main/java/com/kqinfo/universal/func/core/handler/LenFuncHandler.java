package com.kqinfo.universal.func.core.handler;

import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author YangXiaoLong
 * 2023/5/7 23:02
 */
@Component
public class LenFuncHandler extends ObjectFuncHandler {

    @Override
    protected Object doHandle(List<Object> analyticRes) {
        Object o = analyticRes.get(0);
        //给定的参数不存在对应的值时,用-1做处理
        if (Objects.isNull(o)) {
            return -1;
        }
        return analyticRes.get(0).toString().length();
    }

    @Override
    protected int getMethodParamSize() {
        return 1;
    }

    /**
     * 表达式 LEN(#idNo)
     *
     * @return {@link String}
     * @author YangXiaoLong
     * 2023/5/8 9:41
     */
    @Override
    public String funcName() {
        return "LEN";
    }

    @Override
    public String description() {
        return "计算一个给定值的长度";
    }
}
