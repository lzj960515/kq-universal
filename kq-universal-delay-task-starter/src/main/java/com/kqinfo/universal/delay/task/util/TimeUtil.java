package com.kqinfo.universal.delay.task.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public final class TimeUtil {


    private static final long FIVE_SECOND = 5000;

    public static long localDateTime2Millis(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long localDate2Millis(LocalDate date) {
        return localDateTime2Millis(date.atStartOfDay());
    }

    /**
     * 延时的时间 + 当前时间 = 实际时间
     * @param time 时间
     * @param unit 时间单位
     * @return 实际时间
     */
    public static long convert2Millis(long time, TimeUnit unit){
        long delayTime = unit.toMillis(time);
        return System.currentTimeMillis() + delayTime;
    }

    public static int getSecond(long time) {
        return (int) ((time / 1000) % 60);
    }


    public static long getAfterFiveSecond() {
        return System.currentTimeMillis() + FIVE_SECOND;
    }

    public static boolean isInnerFiveSecond(long time) {
        long now = System.currentTimeMillis();
        return now + FIVE_SECOND > time;
    }
}
