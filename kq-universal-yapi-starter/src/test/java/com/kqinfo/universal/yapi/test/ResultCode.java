package com.kqinfo.universal.yapi.test;

/**
 * 返回状态码接口
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface ResultCode {

    /**
     * 获取状态码
     * @return code
     */
    Integer code();

    /**
     * 获取响应消息
     * @return message
     */
    String message();
}
