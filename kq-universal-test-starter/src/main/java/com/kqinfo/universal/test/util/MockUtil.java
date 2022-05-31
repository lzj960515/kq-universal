package com.kqinfo.universal.test.util;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.github.jsonzou.jmockdata.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.0.0
 */
public final class MockUtil {

    public static <T> T mock(Class<T> type) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.setEnabledCircle(true);
        return JMockData.mock(type, mockConfig);
    }

    /**
     * mock多个数据
     *
     * @param type 类型
     * @param num  数量
     * @return {@code list}
     */
    public static <T> List<T> multiMock(Class<T> type, int num) {
        List<T> mockList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            MockConfig mockConfig = new MockConfig();
            mockConfig.setEnabledCircle(true);
            mockList.add(JMockData.mock(type, mockConfig));
        }
        return mockList;
    }

    public static <T> T mock(TypeReference<T> typeReference) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.setEnabledCircle(true);
        return JMockData.mock(typeReference, mockConfig);
    }
}
