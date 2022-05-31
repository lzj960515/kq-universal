package com.kqinfo.universal.delay.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "delay-task")
public class DelayTaskProperties {
    /**
     * success状态任务的保留天数， -1表示永久保存
     */
    private int taskRetentionDays;

    public int getTaskRetentionDays() {
        return taskRetentionDays;
    }

    public void setTaskRetentionDays(int taskRetentionDays) {
        this.taskRetentionDays = taskRetentionDays;
    }
}
