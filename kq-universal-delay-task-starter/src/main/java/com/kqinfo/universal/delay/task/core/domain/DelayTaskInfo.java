package com.kqinfo.universal.delay.task.core.domain;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 延时任务信息
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class DelayTaskInfo {

    /**
     * 任务id：主键
     */
    private Long id;

    /**
     * 任务名：任务的名称，与执行任务的方法对应，用于执行任务时寻找执行任务的方法
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务信息：放置执行任务所需的参数信息
     */
    private String info;

    /**
     * 执行时间 时间戳
     */
    private Long executeTime;

    /**
     * 执行状态：1.创建 2.执行中 3.执行成功 4.执行失败
     */
    private Integer executeStatus;

    /**
     * 执行结果信息
     */
    private String executeMessage;

    public DelayTaskInfo(){}

    public DelayTaskInfo(Long id, String name, String info, Long executeTime) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.executeTime = executeTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getExecuteMessage() {
        return executeMessage;
    }

    public void setExecuteMessage(String executeMessage) {
        this.executeMessage = executeMessage;
    }

    @Override
    public String toString() {
        return "DelayTaskInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", info='" + info + '\'' +
                ", executeTime=" + executeTime +
                ", executeStatus=" + executeStatus +
                ", executeMessage='" + executeMessage + '\'' +
                '}';
    }
}
