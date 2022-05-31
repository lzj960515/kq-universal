package com.kqinfo.universal.log.service;

import com.kqinfo.universal.log.domain.OperateLog;
import com.kqinfo.universal.log.result.PageResult;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
public interface LogRecordService {

    /**
     * 记录日志
     * @param operateLog 日志信息
     */
    void record(OperateLog operateLog);

    /**
     * 分页查询日志列表
     * @param current 当前页
     * @param size 每页显示条数
     * @param category 日志分类
     * @param businessNo 业务编号
     * @return 日志列表
     */
    PageResult<OperateLog> page(int current, int size, String category, String businessNo);

    /**
     * 查询日志信息
     * @param id id
     * @return 日志信息
     */
    OperateLog getById(Long id);
}
