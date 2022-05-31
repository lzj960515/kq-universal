package com.kqinfo.universal.redis.test;

import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
public class Person {

    private LocalDateTime birthday;

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }
}
