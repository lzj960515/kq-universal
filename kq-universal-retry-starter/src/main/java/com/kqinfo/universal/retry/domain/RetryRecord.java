package com.kqinfo.universal.retry.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetryRecord implements Serializable {

    private Long id;
    /**
     * 重试的bean名称, 用于重试时获取bean
     */
    private String beanName;

    /**
     * 重试的方法
     */
    private String method;

    /**
     * 方法参数 json数组
     */
    private String parameter;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 重试间隔：分钟
     */
    private int fixedRate;

    /**
     * 下次执行时间
     */
    private LocalDateTime nextTime;

    /**
     * 状态 1.执行中 2.执行成功 3.执行失败
     */
    private Integer status;
}
