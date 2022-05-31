package com.kqinfo.universal.retry.service;

import com.kqinfo.universal.retry.domain.RetryRecord;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
public interface RetryRecordService {

    /**
     * 保存重试记录
     * @param retryRecord 重试记录
     */
    void record(RetryRecord retryRecord);

    /**
     * 查询执行中的记录列表
     * @return 执行中的记录列表
     */
    List<RetryRecord> listExecutingRecord();

    /**
     * 更加记录状态为成功
     * @param id 记录id
     */
    void updateRecordSuccess(Long id);

    /**
     * 更新重试次数
     * @param id 记录id
     * @param isFail 是否失败
     * @param failReason 此次重试的失败原因
     * @param fixedRate 重试间隔
     */
    void updateRetryTimes(Long id, boolean isFail, String failReason, int fixedRate);
}
