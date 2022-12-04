package com.kqinfo.universal.workflow.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kqinfo.universal.workflow.domain.Task;
import com.kqinfo.universal.workflow.dto.ApproveProgressDto;
import com.kqinfo.universal.workflow.dto.ExecuteTaskDto;
import com.kqinfo.universal.workflow.dto.ProcessDefConfig;
import com.kqinfo.universal.workflow.dto.ProcessStartDto;
import com.kqinfo.universal.workflow.dto.TaskLogDto;
import com.kqinfo.universal.workflow.dto.TodoTaskDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageDto;
import com.kqinfo.universal.workflow.dto.TodoTaskPageParam;

import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.4.0
 */
public interface WorkflowInvoker {

    /**
     * 初始化流程定义
     *
     * @param processDefConfig 流程定义
     */
    void initProcess(ProcessDefConfig processDefConfig);

    /**
     * 通过流程定义名称启动流程, 如果流程存在，取消之前流程并重新发起
     *
     * @param processStartDto 流程启动参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer startProcessByName(ProcessStartDto processStartDto);

    /**
     * 通过流程定义名称启动流程, 如果流程存在，抛出异常
     *
     * @param processStartDto 流程启动参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer startProcessByNameNoCancel(ProcessStartDto processStartDto);

    /**
     * 通过流程定义名称启动流程, 并执行第一个任务, 如果流程存在，取消之前流程并重新发起
     *
     * @param processStartDto 流程启动参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer startProcessAndExecuteFirstTask(ProcessStartDto processStartDto);

    /**
     * 通过流程定义名称启动流程, 并执行第一个任务, 如果流程存在，抛出异常
     *
     * @param processStartDto 流程启动参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer startProcessAndExecuteFirstTaskNoCancel(ProcessStartDto processStartDto);

    /**
     * 执行任务
     *
     * @param executeTaskDto 执行任务参数
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer executeTask(ExecuteTaskDto executeTaskDto);

    /**
     * 驳回任务
     *
     * @param processDefName 流程定义名称
     * @param businessId     业务id
     * @param operator       任务受理人
     * @param reason         原因
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer rejectTask(String processDefName, String businessId, String operator, String reason);

    /**
     * 驳回到上个节点
     *
     * @param processDefName 流程定义名称
     * @param businessId     业务id
     * @param operator       任务受理人
     * @param reason         原因
     * @return 流程状态 1.审核中 2.审核通过 3.驳回
     */
    Integer rejectToPreNode(String processDefName, String businessId, String operator, String reason);

    /**
     * 取消流程
     *
     * @param processDefName 流程定义名称
     * @param businessId     业务id
     * @param operator       取消人
     * @return 流程状态 4.取消审核
     */
    Integer cancelProcess(String processDefName, String businessId, String operator);

    /**
     * 查询10条待办任务，对标首页展示需求
     *
     * @param tenantId       租户id
     * @param operator       用户id
     * @param processDefName 流程名称
     * @return 办任务列表
     */
    List<TodoTaskDto> listTodoTask(String tenantId, String operator, String processDefName);

    /**
     * 查询10条待办任务，对标首页展示需求
     *
     * @param tenantId       租户id
     * @param operator       用户id
     * @param processDefDesc 流程描述
     * @return 办任务列表
     */
    List<TodoTaskDto> listTodoTaskByDesc(String tenantId, String operator, String processDefDesc);

    /**
     * 分页查询待办任务列表
     *
     * @param tenantId          租户id
     * @param operator          任务受理人
     * @param todoTaskPageParam 参数
     * @return 待办任务列表
     */
    IPage<TodoTaskPageDto> pageTodoTask(String tenantId, String operator, TodoTaskPageParam todoTaskPageParam);

    /**
     * 是否有任务
     *
     * @param operator       任务受理人
     * @param processDefName 流程名称
     * @param businessId     业务id
     * @return 是否有任务 1.是 0.否
     */
    Integer hasTask(String operator, String processDefName, String businessId);

    /**
     * 查询审核日志
     *
     * @param businessId     业务id
     * @param processDefName 流程定义名称
     * @return 审核日志
     */
    List<TaskLogDto> listTaskLog(String businessId, String processDefName);

    /**
     * 查询审核进度
     *
     * @param businessId     业务id
     * @param processDefName 流程定义名称
     * @return 审核进度
     */
    List<ApproveProgressDto> approveProgress(String businessId, String processDefName);

    /**
     * 获取任务
     *
     * @param businessId     业务id
     * @param processDefName 流程定义名称
     * @return 任务
     */
    Task getTask(String businessId, String processDefName);

}
