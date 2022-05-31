package com.kqinfo.universal.enums.test;

import com.kqinfo.universal.enums.core.KqEnumHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * @author Zijian Liao
 * @since 2.5.0
 */
@SpringBootTest
public class KqEnumHandlerTest {

    @Test
    public void testGetAll(){
        final Map<String, Map<Object, Object>> all = KqEnumHandler.getAll();
        Assertions.assertEquals(2, all.size());
    }

    @Test
    public void testGet(){
        final Map<Object, Object> orderDict = KqEnumHandler.get("order");
        Assertions.assertEquals("支付订单", orderDict.get(1));
        Assertions.assertEquals("退款订单", orderDict.get(2));
    }
}
