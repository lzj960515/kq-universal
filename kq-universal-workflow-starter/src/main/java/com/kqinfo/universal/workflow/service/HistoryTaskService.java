package com.kqinfo.universal.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kqinfo.universal.workflow.domain.HistoryTask;
import com.kqinfo.universal.workflow.dto.TaskLogDto;

import java.util.List;

/**
 * 历史流程任务 服务类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface HistoryTaskService extends IService<HistoryTask> {

    /**
     * 查询审核日志
     * @param businessId 业务id
     * @param processIds 流程定义id列表
     * @return 审核日志
     */
    List<TaskLogDto> listTaskLog(String businessId, List<Long> processIds);

    /**
     * 查询历史任务列表
     * @param instanceId 流程实例id
     * @return 历史任务列表
     */
    List<HistoryTask> listByInstanceId(Long instanceId);

    /**
     * 查询最后一轮完成的任务
     * @param businessId 业务id
     * @param processIds 流程定义id列表
     * @return 最后一个完成的任务
     */
    List<HistoryTask> getLastTasks(String businessId, List<Long> processIds);

}
