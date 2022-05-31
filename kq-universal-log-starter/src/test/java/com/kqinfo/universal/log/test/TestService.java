package com.kqinfo.universal.log.test;

import com.kqinfo.universal.log.annotation.LogRecord;
import org.springframework.stereotype.Service;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
@Service
public class TestService {

    @LogRecord(template = "订单：{#order.getId()}被修改了", category = "order", businessNo = "#order.getId()", param = "T(com.alibaba.fastjson.JSON).toJSONString(#order)")
    public void test(Order order) {

    }
}
