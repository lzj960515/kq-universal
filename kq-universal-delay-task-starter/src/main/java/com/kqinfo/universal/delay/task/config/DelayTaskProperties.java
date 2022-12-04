package com.kqinfo.universal.delay.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "kq.delay-task")
public class DelayTaskProperties {
    /**
     * success状态任务的保留天数， -1表示永久保存
     */
    private int taskRetentionDays = -1;
    /**
     * 5秒内能够处理任务的数量
     */
    private Integer concurrency = 100;

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public int getTaskRetentionDays() {
        return taskRetentionDays;
    }

    public void setTaskRetentionDays(int taskRetentionDays) {
        this.taskRetentionDays = taskRetentionDays;
    }
}
