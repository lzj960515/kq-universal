package com.kqinfo.universal.retry.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
@Data
public class RetryResult {

    private boolean success;

    private String failReason;

    private LocalDateTime nextTime;
}
