package com.kqinfo.universal.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kqinfo.universal.workflow.domain.TaskOperator;

import java.util.List;

/**
 * 任务处理人关系 服务类
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public interface TaskOperatorService extends IService<TaskOperator> {

    /**
     * 查询任务关联的受理人列表
     *
     * @param taskId 任务id
     * @return 受理人列表
     */
    List<TaskOperator> listByTaskId(Long taskId);

    /**
     * 删除任务关联的受理人列表
     *
     * @param taskId 任务id
     */
    void removeByTaskId(Long taskId);

    /**
     * 保存上个任务的受理人
     *
     * @param parentTaskId 上个任务的任务id
     * @param taskId       当前任务的任务id
     */
    void savePreTaskOperator(Long parentTaskId, Long taskId);

}
