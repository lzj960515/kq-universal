package com.kqinfo.universal.common.util;

import com.kqinfo.universal.common.exception.BusinessException;
import org.springframework.util.StringUtils;

/**
 * 断言工具
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public final class Assert {

    private Assert() {
    }

    public static void notNull(Object value, String message){
        if(value == null){
            throw new BusinessException(message);
        }
    }

    public static void notEmpty(String value, String message){
        if(!StringUtils.hasText(value)){
            throw new BusinessException(message);
        }
    }

    /**
     * 如果有值 抛出异常
     *
     * @param value 值
     * @param message 异常信息
     */
    public static void isEmpty(String value, String message){
        if(StringUtils.hasText(value)){
            throw new BusinessException(message);
        }
    }
}
