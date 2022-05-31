package com.kqinfo.universal.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.regex.Pattern;

/**
 * 时间转LocalDateTime
 * 支持格式：
 * 自定义时间格式yyyy-MM-dd HH:mm[:ss][.sss]，ISO标准时间yyyy-MM-ddTHH:mm[:ss][.sss]，UTC标准时间yyyy-MM-ddTHH:mm:ss[.sss]Z
 * @author Zijian Liao
 * @since 1.0.0
 */
public final class LocalDateTimeUtil {

    private static final String REGEX_TIME = "^(\\d{10,13}|\\d{4}-\\d{2}-\\d{2}.\\d{2}:\\d{2}.*)$";

    private LocalDateTimeUtil(){}

    public static LocalDateTime convert(String resolver) {
        if (Pattern.matches(REGEX_TIME, resolver)) {
            Instant instant;
            switch (resolver.length()) {
                case 10:
                    instant = Instant.ofEpochSecond(Long.parseLong(resolver));
                    return LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));
                case 13:
                    instant = Instant.ofEpochMilli(Long.parseLong(resolver));
                    return LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));
                default:
                    break;
            }

            if (resolver.endsWith("Z")) {
                return LocalDateTime.ofInstant(Instant.parse(resolver), ZoneId.of("GMT+8"));
            } else if (resolver.charAt(10) == 'T') {
                return LocalDateTime.parse(resolver, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else if (resolver.charAt(10) == ' ') {
                return LocalDateTime.parse(resolver, new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .append(DateTimeFormatter.ISO_LOCAL_DATE)
                        .appendLiteral(' ')
                        .append(DateTimeFormatter.ISO_LOCAL_TIME)
                        .toFormatter());
            }
        }
        return null;
    }
}
