package com.kqinfo.universal.common.test;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
public class User {

    private LocalDate birthday;

    private LocalDateTime createTime;
}
