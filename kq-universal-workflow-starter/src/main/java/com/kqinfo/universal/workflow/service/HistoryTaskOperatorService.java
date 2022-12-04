package com.kqinfo.universal.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kqinfo.universal.workflow.domain.HistoryTaskOperator;

import java.util.List;

/**
 * 任务处理人关系 服务类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface HistoryTaskOperatorService extends IService<HistoryTaskOperator> {

    /**
     * 查询任务关联的受理人列表
     *
     * @param taskId 任务id
     * @return 受理人列表
     */
    List<HistoryTaskOperator> listByTaskId(Long taskId);

}
