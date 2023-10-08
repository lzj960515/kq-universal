package com.kqinfo.universal.func.core.handler;

import cn.hutool.core.util.ObjectUtil;
import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 等值比较处理器
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class EqFuncHandler extends ObjectFuncHandler {

    @Override
    protected Object doHandle(List<Object> analyticRes) {
        return ObjectUtil.equal(analyticRes.get(0).toString(), analyticRes.get(1).toString());
    }

    @Override
    protected int getMethodParamSize() {
        return 2;
    }

    /**
     * 表达式 EQ(#age,10)
     *
     * @return {@link String}
     * @author YangXiaoLong
     * 2023/5/8 9:27
     */
    @Override
    public String funcName() {
        return "EQ";
    }

    @Override
    public String description() {
        return "两个值进行比较,如果相等则返回true,如果失败,则返回false";
    }
}
