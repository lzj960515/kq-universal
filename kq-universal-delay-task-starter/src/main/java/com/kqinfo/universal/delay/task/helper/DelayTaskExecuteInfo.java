package com.kqinfo.universal.delay.task.helper;

/**
 * 延时任务执行信息
 * @author Zijian Liao
 * @since 1.0.0
 */
public class DelayTaskExecuteInfo {

    private final Integer executeStatus;

    private final String executeMessage;

    public DelayTaskExecuteInfo(Integer executeStatus){
        this(executeStatus, "");
    }

    public DelayTaskExecuteInfo(Integer executeStatus, String executeMessage) {
        this.executeStatus = executeStatus;
        this.executeMessage = executeMessage;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public String getExecuteMessage() {
        return executeMessage;
    }
}
