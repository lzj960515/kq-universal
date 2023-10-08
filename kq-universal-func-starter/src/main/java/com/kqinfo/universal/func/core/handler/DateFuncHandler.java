package com.kqinfo.universal.func.core.handler;

import cn.hutool.core.date.DateUtil;
import com.kqinfo.universal.func.core.handler.abs.ObjectFuncHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 时间函数处理器
 * -DATE(#pat_brsdate,"yyyy-MM-dd")
 *
 * @author YangXiaoLong
 * 2023/5/8 8:45
 */
@Component
public class DateFuncHandler extends ObjectFuncHandler {

    @Override
    protected Object doHandle(List<Object> analyticRes) {
        Object dateVal = analyticRes.get(0);
        Object dateFormat = analyticRes.get(1);
        if (Objects.isNull(dateVal)) {
            return "";
        }
        return DateUtil.format(DateUtil.parse(dateVal.toString()), String.valueOf(dateFormat));
    }

    @Override
    protected int getMethodParamSize() {
        return 2;
    }

    /**
     * DATE(#pat_brsdate,"yyyy-MM-dd")
     * 取系统时间时,表达式如下
     * DATE(#SYSTIME,"yyyy-MM-dd")
     *
     * @return {@link String}
     * @author YangXiaoLong
     * 2023/5/8 9:09
     */
    @Override
    public String funcName() {
        return "DATE";
    }

    @Override
    public String description() {
        return "将给定时间字段格式化为指定日期格式";
    }
}
