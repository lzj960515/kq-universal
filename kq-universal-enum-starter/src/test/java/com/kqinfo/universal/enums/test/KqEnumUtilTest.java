package com.kqinfo.universal.enums.test;

import com.kqinfo.universal.enums.test.constant.OrderEnum;
import com.kqinfo.universal.enums.test.constant.TestEnum;
import com.kqinfo.universal.enums.util.KqEnumUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 2.5.0
 */
public class KqEnumUtilTest {

    @Test
    public void testGetCodeByDesc(){
        final Object code = KqEnumUtil.getCodeByDesc(TestEnum.class, "desc1");
        Assertions.assertEquals("test1", code);
    }

    @Test
    public void testGetDescByCode(){
        final Object code = KqEnumUtil.getDescByCode(TestEnum.class, "test2");
        Assertions.assertEquals("desc2", code);
    }

    @Test
    public void testGetField1ByField2(){
        final Object value = KqEnumUtil.getField1ByField2(OrderEnum.class, "value", "type", 2);
        Assertions.assertEquals("退款订单", value);
    }
}
