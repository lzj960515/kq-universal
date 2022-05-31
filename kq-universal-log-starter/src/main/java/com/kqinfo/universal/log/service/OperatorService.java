package com.kqinfo.universal.log.service;

import com.kqinfo.universal.log.domain.Operator;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface OperatorService {

    /**
     * 获取用户名
     * @param request {@link HttpServletRequest}
     * @return 操作人信息
     */
    Operator getOperator(HttpServletRequest request);
}
