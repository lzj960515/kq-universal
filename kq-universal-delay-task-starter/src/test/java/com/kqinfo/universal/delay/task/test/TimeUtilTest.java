package com.kqinfo.universal.delay.task.test;

import com.kqinfo.universal.delay.task.util.TimeUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 时间工具测试类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class TimeUtilTest {

    @Test
    public void testLocalDateTime2MillSecond(){
        long l = TimeUtil.localDateTime2Millis(LocalDateTime.now());
        long now = System.currentTimeMillis();
        MatcherAssert.assertThat(Math.abs(l - now) < 10, CoreMatchers.is(true));
    }

    @Test
    public void testLocalDate2Millis(){
        long l = TimeUtil.localDate2Millis(LocalDate.now());
        int i = new Date().compareTo(new Date(l));
        MatcherAssert.assertThat(i, CoreMatchers.is(1));
    }
}
