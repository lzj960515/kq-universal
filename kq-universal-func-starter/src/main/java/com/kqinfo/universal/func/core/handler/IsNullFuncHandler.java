package com.kqinfo.universal.func.core.handler;

import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * isNull表达式处理器
 *
 * @author YangXiaoLong
 * 2023/5/7 22:16
 */
@Component
public class IsNullFuncHandler extends ObjectFuncHandler {

    @Override
    protected Object doHandle(List<Object> analyticRes) {
        return Objects.isNull(analyticRes.get(0)) && !StringUtils.hasText(analyticRes.get(0).toString());
    }

    @Override
    protected int getMethodParamSize() {
        return 1;
    }

    /**
     * 表达式 IN(#idNo)
     *
     * @return {@link String}
     * @author YangXiaoLong
     * 2023/5/8 9:39
     */
    @Override
    public String funcName() {
        return "IN";
    }

    @Override
    public String description() {
        return "判断一个值是否为空,如果为空,则返回true,否则返回false";
    }
}
