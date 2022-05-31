package com.kqinfo.universal.retry.service;

import com.kqinfo.universal.retry.domain.RetryRecord;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
public interface RetryAlarmService {

    /**
     * 告警
     * @param retryRecord 重试记录
     */
    void alarm(RetryRecord retryRecord);

}
