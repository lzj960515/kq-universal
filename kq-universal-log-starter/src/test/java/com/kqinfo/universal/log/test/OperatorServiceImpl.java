package com.kqinfo.universal.log.test;

import com.kqinfo.universal.log.domain.Operator;
import com.kqinfo.universal.log.service.OperatorService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
@Service
public class OperatorServiceImpl implements OperatorService {
    @Override
    public Operator getOperator(HttpServletRequest request) {
        final Operator operator = new Operator();
        operator.setUserId("1");
        operator.setUsername("zhangsan");
        return operator;
    }
}
