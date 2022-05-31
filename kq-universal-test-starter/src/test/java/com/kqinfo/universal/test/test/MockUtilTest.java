package com.kqinfo.universal.test.test;

import com.github.jsonzou.jmockdata.JMockData;
import com.kqinfo.universal.test.util.MockUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * @author Zijian Liao
 * @since 2.0.0
 */
public class MockUtilTest {

    @Test
    public void testMockUtil(){
        User mock = MockUtil.mock(User.class);
        MatcherAssert.assertThat(mock.getHobbies(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mock.getOrder(), CoreMatchers.notNullValue());
    }

    @Test
    public void testRepeatMock(){
        Hobby mock = MockUtil.mock(Hobby.class);
        Hobby mock2 = MockUtil.mock(Hobby.class);
        MatcherAssert.assertThat(mock, CoreMatchers.not(mock2));
    }
}
